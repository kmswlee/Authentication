import React, {useState} from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { useNavigate, useLocation } from 'react-router';
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

export default function SignUp() {
    const navi = useNavigate();
    const state = useLocation();
    const [values,setValues] = useState({
      email : '',
      userName : ''
    });

    const [guideTxts, setGuideTxts] = useState({
        nameGuide : ''
    });

    const [error, setError] = useState({
        nameError: '',
      })

    

    const onTextCheck = () => {
    let nameError = "";
    
    if (values.userName.length === 0) nameError = "이름을 입력해주세요.";

    setError({
      nameError, 
    })

    if (nameError) return false;

    return true;
    }

    const handleChangeForm = (e) => {
        setValues({ 
            ...values, 
            [e.target.name]: e.target.value 
        });
    }

    const handleSubmit = async (event) => {
        event.preventDefault();
        const valid = onTextCheck();
        // const config = {
        //   headers: {Authorization:`Bearer ${state.state.Authorization}`}
        // }
        const data = new FormData(event.currentTarget);
        if (!valid){
            console.log('retry!');
        }
        else{
            await axios
                  .get('http://localhost:8000/user-service/find/password', {
                    params: {
                      email: data.get('email'),
                      userName: data.get('userName')
                    }
                  })
                  .then(()=> {
                    alert('임시 비밀번호를 전송하였습니다.');
                    navi('/');
                  })
                  .catch(()=> {
                    alert('비밀번호 전송에 실패하였습니다. 메일 또는 이름을 확인해주세요.');
                  })

        }
  };

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
            비밀번호 찾기
          </Typography>
          <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 3 }}>
            <Grid container spacing={2}>
            <Grid item xs={12}>
                    <TextField
                    required
                    fullWidth
                    id="email"
                    label="Email Address"
                    name="email"
                    autoComplete="email"
                    value={values.email}
                    onChange={handleChangeForm}
                    />
                </Grid>
                {
                    error.emailError
                        ? 
                        <div style={{ color: "red", fontSize: "15px", margin: '15px 0 10px 15px' }}>{error.emailError}</div>
                        :
                        <div style={{ color: "gray", fontSize: "15px", margin: '15px 0 10px 15px' }}>{guideTxts.emailGuide}</div>
                }

                <Grid item xs={12}>
                    <TextField
                        required
                        fullWidth
                        id="userName"
                        label="userName"
                        name="userName"
                        autoComplete="userName"
                        value={values.userName}
                        onChange={handleChangeForm}
                    />
                </Grid>
                {
                    error.nameError 
                        ? 
                            <div style={{ color: "red", fontSize: "15px", margin: '15px 0 10px 15px' }}>{error.nameError}</div>
                        :
                            <div style={{ color: "gray", fontSize: "15px", margin: '15px 0 10px 15px' }}>{guideTxts.nameGuide}</div>
                }
                
            </Grid>
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              비밀번호 찾기
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