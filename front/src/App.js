import React from 'react';
import {BrowserRouter,Route, Routes} from "react-router-dom";
import './App.css';
import './assets/css/bootstrap.css';
import SignIn from './pages/SignIn';
import SignUp from './pages/SignUp';
import MyPage from './pages/MyPage';
import Modify from './pages/Modify';
import MyPageAdmin from './pages/MyPageAdmin';
import ForgetPassword from './pages/ForgetPassword';
function App() {
  return (
    <BrowserRouter>
    <Routes>
      <Route exact path='/' element={<SignIn/>} />
      <Route exact path='/SignUp' element={<SignUp/>} />
      <Route exact path='/MyPage' element={<MyPage/>} />
      <Route exact path='/Modify' element={<Modify/>} />
      <Route exact path='/MyPageAdmin' element={<MyPageAdmin/>} />
      <Route exact path='/ForgetPassword' element={<ForgetPassword/>} />

    </Routes>
    </BrowserRouter>
  );
}

export default App;
