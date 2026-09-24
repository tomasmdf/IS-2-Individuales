#!/usr/bin/env python3
"""
========================================================================================
EJECUTOR AUTÓNOMO DE PRUEBAS DE ESTRÉS: stress_test_runner.py
========================================================================================
Script multi-hilo independiente (solo utiliza la biblioteca estándar de Python 3).
Permite ejecutar pruebas de estrés inmediatas sin requerir la instalación de Locust ni JMeter.

Métricas recolectadas:
- Throughput (RPS - Requests Per Second)
- Latencia promedio, mínima y máxima (ms)
- Percentiles: p50, p90, p95, p99 (ms)
- Tasa de éxito y conteo de fallos

Uso:
    python stress_test_runner.py --concurrency 50 --requests 1000 --url http://localhost:8080
========================================================================================
"""

import argparse
import concurrent.futures
import json
import statistics
import time
import urllib.error
import urllib.parse
import urllib.request

DNIS_PRUEBA = ["32456789", "33987456", "52145896", "29876543", "50321654", "25412365"]

def ejecutar_peticion(base_url, idx):
    """
    Ejecuta una petición individual de verificación biométrica por DNI.
    """
    dni = DNIS_PRUEBA[idx % len(DNIS_PRUEBA)]
    url = f"{base_url}/accesos/buscar-dni?dni={urllib.parse.quote(dni)}"

    inicio = time.perf_counter()
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "StressTestRunner/1.0", "Accept": "application/json"})
        with urllib.request.urlopen(req, timeout=5.0) as resp:
            data = resp.read()
            duracion_ms = (time.perf_counter() - inicio) * 1000.0
            return (True, resp.status, duracion_ms)
    except urllib.error.HTTPError as e:
        duracion_ms = (time.perf_counter() - inicio) * 1000.0
        # 400 puede ocurrir si no existe, cuenta como respondido
        return (e.code == 400, e.code, duracion_ms)
    except Exception as e:
        duracion_ms = (time.perf_counter() - inicio) * 1000.0
        return (False, 0, duracion_ms)

def main():
    parser = argparse.ArgumentParser(description="Ejecutor de Pruebas de Estrés para Club Deportivo")
    parser.add_argument("--url", default="http://localhost:8080", help="URL base del servidor")
    parser.add_argument("--concurrency", type=int, default=50, help="Cantidad de hilos concurrentes")
    parser.add_argument("--requests", type=int, default=500, help="Total de peticiones a ejecutar")
    args = parser.parse_args()

    print("=" * 80)
    print(" INICIANDO PRUEBA DE CARGA Y ESTRÉS - SISTEMA CLUB DEPORTIVO")
    print("=" * 80)
    print(f" Servidor Objetivo : {args.url}")
    print(f" Concurrencia      : {args.concurrency} hilos simultáneos")
    print(f" Peticiones Totales: {args.requests}")
    print("=" * 80)

    latencias = []
    exitosos = 0
    fallidos = 0

    t_inicio_total = time.perf_counter()

    with concurrent.futures.ThreadPoolExecutor(max_workers=args.concurrency) as executor:
        futuros = [executor.submit(ejecutar_peticion, args.url, i) for i in range(args.requests)]
        for f in concurrent.futures.as_completed(futuros):
            exito, status, duracion = f.result()
            latencias.append(duracion)
            if exito:
                exitosos += 1
            else:
                fallidos += 1

    t_total = time.perf_counter() - t_inicio_total
    rps = args.requests / t_total if t_total > 0 else 0

    latencias_ordenadas = sorted(latencias)
    p50 = statistics.median(latencias_ordenadas) if latencias else 0
    p90 = latencias_ordenadas[int(len(latencias_ordenadas) * 0.90)] if latencias else 0
    p95 = latencias_ordenadas[int(len(latencias_ordenadas) * 0.95)] if latencias else 0
    p99 = latencias_ordenadas[int(len(latencias_ordenadas) * 0.99)] if latencias else 0
    promedio = statistics.mean(latencias) if latencias else 0

    print("\n" + "=" * 80)
    print(" RESULTADOS Y MÉTRICAS DE RENDIMIENTO")
    print("=" * 80)
    print(f" Tiempo Total de Ejecución : {t_total:.2f} segundos")
    print(f" Throughput Logrado        : {rps:.2f} Requests/segundo (RPS)")
    print(f" Peticiones Exitosas       : {exitosos} ({exitosos / args.requests * 100:.1f}%)")
    print(f" Peticiones Fallidas       : {fallidos} ({fallidos / args.requests * 100:.1f}%)")
    print("-" * 80)
    print(" DISTRIBUCIÓN DE LATENCIAS:")
    print(f"  Latencia Mínima          : {min(latencias):.2f} ms")
    print(f"  Latencia Promedio        : {promedio:.2f} ms")
    print(f"  Percentil 50 (Mediana)   : {p50:.2f} ms")
    print(f"  Percentil 90 (p90)       : {p90:.2f} ms")
    print(f"  Percentil 95 (p95)       : {p95:.2f} ms")
    print(f"  Percentil 99 (p99)       : {p99:.2f} ms")
    print(f"  Latencia Máxima          : {max(latencias):.2f} ms")
    print("=" * 80)

    # Diagnóstico preliminar según SLA
    print(" EVALUACIÓN DEL SLA (SERVICE LEVEL AGREEMENT):")
    if p95 < 250 and fallidos == 0:
        print(" [APROBADO EXCELENTE] El sistema supera el SLA: p95 < 250ms con 0% de errores.")
    elif p95 < 500 and (fallidos / args.requests) < 0.01:
        print(" [APROBADO SATISFACTORIO] Latencia aceptable para horas de alta demanda.")
    else:
        print(" [ALERTA DE RENDIMIENTO] Se sugiere aumentar el pool HikariCP o escalar réplicas.")
    print("=" * 80)

if __name__ == "__main__":
    main()
