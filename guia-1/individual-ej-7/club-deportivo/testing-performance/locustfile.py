"""
========================================================================================
SUITE DE PRUEBAS DE CARGA Y ESTRÉS: locustfile.py (LOCUST FRAMEWORK)
========================================================================================
Este script implementa pruebas de carga distribuida y de estrés sobre el sistema del
Club Deportivo utilizando el framework open-source Locust de Python.

Objetivos de la prueba:
1. Simular concurrencia masiva de molinetes en horario pico (Entradas y Salidas simultáneas).
2. Medir tiempos de respuesta en la verificación fotográfica por DNI (< 200 ms esperado).
3. Evaluar la concurrencia en la cobranza de cuotas con diversos medios de pago.
4. Identificar el punto de saturación del pool de conexiones HikariCP y del servidor Tomcat.

Instalación y ejecución:
    pip install locust
    locust -f locustfile.py --host=http://localhost:8080

Modo Headless (Consola directa para CI/CD):
    locust -f locustfile.py --headless -u 200 -r 20 -t 2m --host=http://localhost:8080
========================================================================================
"""

from locust import HttpUser, task, between
import random

class ClubDeportivoLoadTest(HttpUser):
    # Simula un tiempo de espera aleatorio entre 0.5 y 2.0 segundos entre cada acción del operador
    wait_time = between(0.5, 2.0)

    # Conjunto de DNIs de prueba pre-cargados en la base de datos
    dnis_prueba = [
        "32456789",  # Carlos Gómez (Titular al día)
        "33987456",  # Mariana Pérez (Familiar de Carlos Gómez)
        "52145896",  # Tomás Gómez (Familiar de Carlos Gómez)
        "29876543",  # Laura Martínez (Titular adeuda mes)
        "50321654",  # Sofía Martínez (Familiar de Laura Martínez)
        "25412365",  # Roberto Rossi (Moroso / Cuotas vencidas)
    ]

    def on_start(self):
        """
        Método de inicio para cada usuario simulado.
        Autentica la sesión como operador de recepción.
        """
        response = self.client.get("/login")
        # En una ejecución real contra Spring Security con CSRF, se extrae el token del HTML.
        # Para pruebas de carga perimetral, se envía la petición de login:
        self.client.post("/login", data={
            "username": "recepcion",
            "password": "recep123"
        })

    @task(5)
    def consultar_monitor_dashboard(self):
        """
        Escenario 1: Navegación y consulta del Dashboard con indicadores operativos.
        Peso: 5 (frecuente)
        """
        self.client.get("/", name="[GET] / Dashboard Operativo")

    @task(10)
    def buscar_persona_molinete(self):
        """
        Escenario 2: Búsqueda perimetral rápida por DNI para cotejo de fotografía facial.
        Peso: 10 (Operación principal de molinetes en hora pico)
        """
        dni = random.choice(self.dnis_prueba)
        with self.client.get(f"/accesos/buscar-dni?dni={dni}",
                             name="[AJAX] /accesos/buscar-dni",
                             catch_response=True) as response:
            if response.status_code == 200:
                data = response.json()
                # Verifica que retorne la foto del rostro y el estado financiero
                if "fotoRostro" in data and "estadoCuota" in data:
                    response.success()
                else:
                    response.failure("Respuesta incompleta: falta fotoRostro o estadoCuota")
            elif response.status_code == 400:
                response.success() # DNI no encontrado esperado en caso de prueba
            else:
                response.failure(f"Código de estado inesperado: {response.status_code}")

    @task(8)
    def registrar_acceso_molinete(self):
        """
        Escenario 3: Registro de Entrada o Salida física en el molinete.
        Peso: 8 (Muy frecuente)
        """
        dni = random.choice(self.dnis_prueba)
        tipo = random.choice(["ENTRADA", "SALIDA"])
        self.client.post("/accesos/registrar", data={
            "dniPersona": dni,
            "tipoAcceso": tipo,
            "puntoAcceso": "Molinete Principal #1",
            "observaciones": "Prueba de carga automatizada"
        }, name="[POST] /accesos/registrar (Entrada/Salida)")

    @task(3)
    def consultar_padron_socios(self):
        """
        Escenario 4: Consulta del padrón general de socios con filtro de búsqueda.
        Peso: 3
        """
        self.client.get("/socios?criterio=Gomez", name="[GET] /socios?criterio=Filtro")

    @task(2)
    def registrar_pago_cuota(self):
        """
        Escenario 5: Registro transaccional de cobranza de cuotas con diversos medios de pago.
        Peso: 2
        """
        medio = random.choice(["EFECTIVO", "TRANSFERENCIA", "MERCADO_PAGO"])
        # Simula el intento de cobranza de cuotas
        self.client.post("/pagos/guardar", data={
            "socioId": 1,
            "periodoMes": random.randint(1, 12),
            "periodoAnio": random.randint(2027, 2030), # Años futuros para evitar colisión de clave única
            "monto": "15000.00",
            "medioPago": medio,
            "observaciones": f"Pago de estrés simulado con {medio}"
        }, name=f"[POST] /pagos/guardar ({medio})")
