import React, { useState } from 'react';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Input from "./Input";

const LoginForm = ({ onSubmit, onSubmitReg }) => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    let handleUserNameChange = event => setUsername(event.target.value);

    let handlePasswordChange = event => setPassword(event.target.value);

    let handleSubmit = () => {
        onSubmit(username, password);
    }

    // let handleSubmitAnon = () => {
    //     setUid(true);
    // }
    // let getName = (username) =>{
    //     onSubmitAnon(username);
    // }
    let handleReg = () => {
        onSubmitReg();
    }

    return (
        <div>
            <TextField
                label="Type your username"
                placeholder="Username"
                onChange={handleUserNameChange}
                margin="normal"
                onKeyPress={event => {
                    if (event.key === 'Enter') {
                        handleSubmit();
                    }
                }}
            />

            <TextField
                label="Type your pass"
                placeholder="Password"
                onChange={handlePasswordChange}
                margin="normal"
                onKeyPress={event => {
                    if (event.key === 'Enter') {
                        handleSubmit();
                    }
                }}
            />
            <br />
            <Button variant="contained" color="primary" onClick={handleSubmit} >
                Login
             </Button>
            <Button onClick={handleReg}>
                Registration
            </Button>


        </div>
    )
}

export default LoginForm
