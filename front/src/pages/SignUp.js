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
import { useNavigate } from 'react-router';
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

    const [values, setValues] = useState({
        email: '',
        password: '',
        confirmPassword: '',
        name: ''
    })

    const [guideTxts, setGuideTxts] = useState({
        emailGuide : '이메일 형식에 맞게 작성해 주세요.',
        pwdGuide : '숫자와 문자를 조합해서 최소 8글자는 입력해 주세요.',
        confirmPwdGuide : '한번더 입력해 주세요.',
        nameGuide : ''
    });

    const [error, setError] = useState({
        emailError: '',
        confirmPwd: '',
        nameError: '',
      })

    const isEmail = email => {
        const emailRegex = /^(([^<>()\].,;:\s@"]+(\.[^<>()\].,;:\s@"]+)*)|(".+"))@(([^<>()¥[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/i; 
        return emailRegex.test(email);
    };  

    const confirmPassword = (pass, confirmPass) => {
        return pass === confirmPass
    }

    const onTextCheck = () => {
    let emailError = "";
    let confirmPwd = "";
    let nameError = "";
    
    if (!isEmail(values.email)) emailError = "email 형식이 아닙니다.";
    if (!confirmPassword(values.password, values.confirmPassword)) confirmPwd = "비밀번호가 일치하지 않습니다.";
    if (values.userName.length === 0) nameError = "이름을 입력해주세요.";

    setError({
        emailError, confirmPwd, nameError, 
    })

    if (emailError ||  confirmPwd || nameError) return false;

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
        const data = new FormData(event.currentTarget);
        if (!valid){
            console.log('retry!');
        }
        else{
            await axios
                .post('http://localhost:8000/user-service/users', {
                    email: data.get('email'),
                    userName: data.get('userName'),
                    password: data.get('password'),
                })
                .then((res) => {
                alert('회원가입 성공')
                navi('/');
                })
                .catch(() => {
                alert('아이디 또는 비밀번호가 틀렸습니다.');
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
            Sign up
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
                <Grid item xs={12}>
                    <TextField
                    required
                    fullWidth
                    name="password"
                    label="Password"
                    type="password"
                    id="password"
                    autoComplete="new-password"
                    value={values.password}
                    onChange={handleChangeForm}
                    />
                </Grid>
                {
                    error.pwdError 
                        ? 
                            <div style={{ color: "red", fontSize: "15px", margin: '15px 0 10px 15px' }}>{error.pwdError}</div>
                        :
                            <div style={{ color: "gray", fontSize: "15px", margin: '15px 0 10px 15px' }}>{guideTxts.pwdGuide}</div>
                }
                <Grid item xs={12}>
                    <TextField
                    required
                    fullWidth
                    name="confirmPassword"
                    label="PasswordConfirm"
                    type="password"
                    id="passwordConfirm"
                    autoComplete="new-password"
                    value={values.confirmPassword}
                    onChange={handleChangeForm}
                    />
                </Grid>
                {
                    error.confirmPwd
                        ? 
                            <div style={{ color: "red", fontSize: "15px", margin: '15px 0 10px 15px' }}>{error.confirmPwd}</div>
                        :
                            <div style={{ color: "gray", fontSize: "15px", margin: '15px 0 10px 15px' }}>{guideTxts.confirmPwdGuide}</div>
                }
            </Grid>
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              Sign Up
            </Button>
            <Grid container justifyContent="flex-end">
              <Grid item>
                <Link href="/" variant="body2">
                  Already have an account? Sign in
                </Link>
              </Grid>
            </Grid>
          </Box>
        </Box>
        <Copyright sx={{ mt: 5 }} />
      </Container>
    </ThemeProvider>
  );
}