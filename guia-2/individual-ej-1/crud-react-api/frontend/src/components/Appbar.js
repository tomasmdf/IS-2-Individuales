import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { Link as RouterLink } from 'react-router-dom';

export default function ButtonAppBar() {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h5" component={RouterLink} to="/" color="inherit" sx={{ flexGrow: 1, textDecoration: 'none' }}>
            Products List
          </Typography>
          <Button color="inherit" component={RouterLink} to="/">Products</Button>
        </Toolbar>
      </AppBar>
    </Box>
  );
}
