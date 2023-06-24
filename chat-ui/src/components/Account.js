import React, {useState} from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import categoryapi from "../services/categoryapi";
import chatapi from "../services/chatapi";
const Account =({currentAcc, type}) => {
    const [isEmail, setIsEmail] = useState(false);
    const [isPass, setIsPass] = useState(false);
    const [newEmail, setNewEmail] = useState("");
    const [newPass, setNewPass] = useState("");

    let handleEmailChange = event => setNewEmail(event.target.value);
    let handlePassChange = event => setNewPass(event.target.value);

    let changeEmail = () => {
        setIsEmail(true);
        setNewEmail(currentAcc.email);
    }
    let changePass = () => {
        setIsPass(true);
        setNewEmail(currentAcc.email);
    }

    let cancel = () => {
        setIsPass(false);
        setIsEmail(false);
        setNewEmail("");
        setNewPass("");
    }

    let updateAccount = () => {
        console.log(newEmail, newPass);
        // if(newEmail===""){
        //     setNewEmail(currentAcc.email);
        // }
        if(type!=="player"){
            chatapi.updateCurrent(currentAcc.username, newEmail, newPass, currentAcc.id).then(res => {
                if(res.status===200){
                    currentAcc.email=newEmail;
                    cancel();
                }
            }).catch(() => {
                console.log('Error Occured while creating role to api');
            });
        }
    }

    return (
        <>
            <div className="chat">
            <ul className="accInfo">
                <li>
                    <p>username: {currentAcc.username}</p>
                </li>
                <li>
                    <p>email: {currentAcc.email}</p>
                </li>
                {type!=="player" &&(
                    <li>
                        <p>role: {currentAcc.roleName}</p>
                    </li>
                )}
                <Button onClick={changeEmail}>
                    Change email
                </Button>
                <Button onClick={changePass}>
                    Change password
                </Button>
                {(isEmail || isPass) && (
                    <>
                        <Button onClick={updateAccount}>
                            Save
                        </Button>
                        <Button onClick={cancel}>
                            Cancel
                        </Button>
                    </>

                )}
            </ul>
                {isEmail &&(
                    <TextField
                        label="Type new Email"
                        placeholder="email"
                        onChange={handleEmailChange}
                        margin="normal"
                    />
                )}
                {isPass &&(
                    <TextField
                        label="Type new password"
                        placeholder="password"
                        onChange={handlePassChange}
                        margin="normal"
                    />
                )}
            </div>
        </>
    )
}
export default Account