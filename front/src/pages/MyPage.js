import React, {useState} from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
function Copyright(props) {
  return (
    <Typography variant="body2" color="text.secondary" align="center" {...props}>
      {'Copyright © '}
      <Link color="inherit" href="https://github.com/kmswlee/Authentication">
        Your Website
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
}

const theme = createTheme();

export default function MyPage(res) {
  const navi = useNavigate();
  const state = useLocation();
  console.log(state);
  const config = {
    headers: {Authorization:`Bearer ${state.state.Authorization}`}
  }
  const [value, setValue] = useState({
    email: state.state.email,
    userName: state.state.userName,
    uid: state.state.uid,
    authorization: state.state.Authorization
})
  const secess = async (e) => {
      e.preventDefault();
      await axios
            .delete(`http://localhost:8000/user-service/users/${state.state.uid}`, config)
            .then(() => {
              alert('회원 탈퇴 하셨습니다.');
              navi('/')
            })
            .catch(() => {
              alert('회원 탈퇴를 실패하셨습니다.');
            })
  };
  const modified = async (e) => {
    e.preventDefault();
    navi('/Modify',{state:{email:value.email, uid:value.uid, userName:value.userName, Authorization: value.authorization}})
  };
  const logout = async (e) => {
    e.preventDefault();
    alert('로그아웃 되었습니다.');
    navi('/');
  }

  return (
    <ThemeProvider theme={theme}>
      <Container component="main" maxWidth="xs">
        <CssBaseline />
        <Box
          sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
            <LockOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5">
            My Page
          </Typography>
          <Box component="form" noValidate sx={{ mt: 3 }}>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                  {value.email}
                </Grid>
                
                <Grid item xs={12}>
                   {value.userName}
                </Grid>
               
            </Grid>
            <Button
              onClick={modified}
              // type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              정보수정
            </Button>
            <Button
              onClick={secess}
              // type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              탈퇴하기
            </Button>
            <Button
              onClick={logout}
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              로그아웃
            </Button>
            <Grid container justifyContent="flex-end">
              <Grid item>
                
              </Grid>
            </Grid>
          </Box>
        </Box>
        <Copyright sx={{ mt: 5 }} />
      </Container>
    </ThemeProvider>
  );
}