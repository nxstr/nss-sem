import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import React, {useState} from "react";

const RegForm = ({onSubmit}) => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    let handleUserNameChange = event => setUsername(event.target.value);

    let handlePasswordChange = event => setPassword(event.target.value);

    let handleEmailChange = event => setEmail(event.target.value);

    let handleSubmit = () => {
        onSubmit(username, password, email);
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
            <TextField
                label="Type your email"
                placeholder="Email"
                onChange={handleEmailChange}
                margin="normal"
                onKeyPress={event => {
                    if (event.key === 'Enter') {
                        handleSubmit();
                    }
                }}
            />
            <br />
            <Button variant="contained" color="primary" onClick={handleSubmit} >
                Register
            </Button>


        </div>
    )
}

export default RegForm