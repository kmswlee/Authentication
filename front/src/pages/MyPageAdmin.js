import React, {useState, useEffect} from 'react';
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
  const [datas,setData] = useState([]);
  console.log(state.state.Authorization)
  const config = {
    headers: {Authorization:`Bearer ${state.state.Authorization}`}
  };
  const [value, setValue] = useState({
    email: state.state.email,
    userName: state.state.userName,
    uid: state.state.uid,
    authorization: state.state.Authorization
  });

  useEffect(() => {
    const userData = async () => {
    await axios
          .get('http://localhost:8000/user-service/admin/users',config)
          .then((res) => {
            setData(res.data)
          })
        };
    userData();
  },[]);

  const userList = datas.map((item,idx) => (
      <>
      <Grid item xs={12}>
        {item.email}
      </Grid>
      <Grid item xs={12}>
        {item.userName}
      </Grid>
      <Button onClick={()=> ejection(item.userId)} variant="contained" sx={{ mt: 2, mb: 2 }}> 강퇴 </Button>
      </>
  ));

  const ejection = async (uid) => {
    await axios
          .delete(`http://localhost:8000/user-service/admin/${uid}`, config)
          .then(() => {
            alert('강퇴 되었습니다.');
            navi('/MyPageAdmin',{state:{email:value.email, uid:value.uid, userName:value.userName, Authorization: value.authorization}});
          })
          .catch(()=> {
            alert('강퇴를 실패 하였습니다.');
          })
  }

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
            My Page Admin
          </Typography>
          <Box component="form" noValidate sx={{ mt: 3 }}>
            <Grid container spacing={1}>
              {userList}
            </Grid>
            
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