import React, {useState} from "react";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import categoryapi from "../services/categoryapi";

const Players = ({players, submitPlayer})=>{
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isNew, setIsNew] = useState(false);
    const [updatePass, setUpdatePass] = useState(false);
    const [saveAccess, setSaveAccess] = useState(false);
    const [pid, setPid] = useState(-1);
    const [errMess, setErrMess] = useState("");

    let handleNameChange = event => setName(event.target.value);

    let handleEmailChange = event => setEmail(event.target.value);

    let handlePassChange = event => setPassword(event.target.value);

    let handleSubmit = () => {
        console.log("enter");
    }

    let handleUpdatePassword = () =>{
        setUpdatePass(true);
    }

    let handleOpen = (event, id) => {
        setIsNew(true);
        categoryapi.getPlayer(id).then(res => {
            setName(res.data.username);
            setEmail(res.data.email);
            setPassword("");
            setPid(id);
            setSaveAccess(true);
            setErrMess("");
        }).catch(err => {
            setErrMess(err?.response?.data);
        })
    }

    let renderPlayer = (player) => {
        return (
            <>
                <li className="Messages-list">
                <span
                    className="avatar"
                    style={{ backgroundColor: "yellow" }}
                />
                    <div className="Message-content-1">
                        <div className="username">
                            {player.id}
                        </div>
                        <div className="text">{player.username}</div>
                    </div>
                    <Button variant="contained" color="primary" onClick={event => handleOpen(event, player.id)} >
                        Open
                    </Button>
                </li>
            </>
        );
    }

    let savePlayer = () => {
        setSaveAccess(false);
            categoryapi.updatePlayer(name, email, password, pid).then(res => {
                if(res.status===200){
                    back();
                }
                setErrMess("");
            }).catch(err => {
                setErrMess(err?.response?.data);
                setSaveAccess(true);
            })

    }

    let back = () => {
        setIsNew(false);
        setName("");
        setEmail("");
        setPassword("");
        setUpdatePass(false);
        setPid(-1);
        submitPlayer();
    }


    return (
        <>
            {errMess!=="" && (
                <p>{errMess}</p>
            )}
            {!!isNew ? (
                    <>
                        <TextField
                            label="Type username"
                            value={name}
                            placeholder="username"
                            onChange={handleNameChange}
                            margin="normal"
                            onKeyPress={event => {
                                if (event.key === 'Enter') {
                                    handleSubmit();
                                }
                            }}
                        />
                        <TextField
                            label="Type email"
                            value={email}
                            placeholder="email"
                            onChange={handleEmailChange}
                            margin="normal"
                            onKeyPress={event => {
                                if (event.key === 'Enter') {
                                    handleSubmit();
                                }
                            }}
                        />
                                    <Button onClick={handleUpdatePassword}>
                                        Change password
                                    </Button>
                                    {updatePass &&(
                                        <TextField
                                            label="Type new password"
                                            placeholder="password"
                                            onChange={handlePassChange}
                                            margin="normal"
                                            onKeyPress={event => {
                                                if (event.key === 'Enter') {
                                                    handleSubmit();
                                                }
                                            }}
                                        />
                                    )}
                        {saveAccess &&(
                            <Button onClick={savePlayer}>
                                Save
                            </Button>
                        )}

                        <Button onClick={back}>
                            Cancel
                        </Button>
                    </>
                ):
                <>

                    <ul className="chat-list">
                        {players.map(cat => renderPlayer(cat))}
                    </ul>
                </>
            }
        </>
    )
}
export default Players