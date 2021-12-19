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
    const [values, setValues] = useState({
        password: '',
        confirmPassword: '',
        name: '',
        uid: state.state.uid,
        authorization: state.state.Authorization
    })

    const [guideTxts, setGuideTxts] = useState({
        pwdGuide : '숫자와 문자를 조합해서 최소 8글자는 입력해 주세요.',
        confirmPwdGuide : '한번더 입력해 주세요.',
        nameGuide : ''
    });

    const [error, setError] = useState({
        confirmPwd: '',
        nameError: '',
      })

    const confirmPassword = (pass, confirmPass) => {
        return pass === confirmPass
    }

    const onTextCheck = () => {
    let confirmPwd = "";
    let nameError = "";
    
    if (!confirmPassword(values.password, values.confirmPassword)) confirmPwd = "비밀번호가 일치하지 않습니다.";
    if (values.userName.length === 0) nameError = "이름을 입력해주세요.";

    setError({
      confirmPwd, nameError, 
    })

    if (confirmPwd || nameError) return false;

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
        const config = {
          headers: {Authorization:`Bearer ${state.state.Authorization}`}
        }
        const data = new FormData(event.currentTarget);
        if (!valid){
            console.log('retry!');
        }
        else{
            await axios
                .put(`http://localhost:8000/user-service/users/${state.state.uid}`,{
                    userName: data.get('userName'),
                    password: data.get('password'),
                },config)
                .then((res) => {
                alert('회원정보 수정 성공')
                navi('/MyPage',{state:{email:res.data.email,uid:values.uid ,userName:res.data.userName, Authorization:values.authorization}});
                })
                .catch(() => {
                alert('회원 정보 수정에 실패하였습니다.');
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
            회원정보 수정
          </Typography>
          <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 3 }}>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                  {state.state.email}
                </Grid>

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
              회원정보 수정
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