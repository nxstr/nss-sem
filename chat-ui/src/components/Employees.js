import TextField from "@material-ui/core/TextField";
import React, {useState} from "react";
import Button from "@material-ui/core/Button";
import categoryapi from "../services/categoryapi";

const Employees =({employees, roles, submitEmployee}) => {
    const [isNew, setIsNew] = useState(false);
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [uid, setUid] = useState(false);
    const [updatePass, setUpdatePass] = useState(false);
    const [savedRoleName, setSavedRoleName] = useState("");
    const [selectedRoleId, setSelectedRoleId] = useState(-1);
    const [savedCats, setSavedCats] = useState([]);
    const [cats, setCats] = useState([]);
    const [saveAccess, setSaveAccess] = useState(false);
    const [eid, setEid] = useState(-1);
    const [errMess, setErrMess] = useState("");

    let openNew = () => {
        setIsNew(true);
        setSaveAccess(true);
        if(roles.length!==0){
            setSelectedRoleId(roles[0].id);
            setCats(roles[0].categoryDtoList);
            setSavedRoleName("");
            setUid(false);
            setUpdatePass(false);
            setSavedCats([]);
        }
    }

    let handleNameChange = event => setName(event.target.value);

    let handleEmailChange = event => setEmail(event.target.value);

    let handlePassChange = event => setPassword(event.target.value);

    let handleSubmit = () => {
        console.log("enter");
    }

    let handleUpdatePassword = () =>{
        setUpdatePass(true);
    }

    let renderOption = (option) => {
        return (
            <option key={option.id} value={option.id}>
                {option.name}
            </option>
        );
    }

    let handleChange = event => {
        setSelectedRoleId(event.target.value);
        for(let i=0; i<roles.length; i++){
            if(roles[i].id.toString()===event.target.value){
                setCats(roles[i].categoryDtoList);
            }
        }
    }

    let saveEmployee = () => {
        setSaveAccess(false);
        if(!uid){
            categoryapi.createEmployee(name, email, password, selectedRoleId).then(res => {
                if(res.status===200){
                    back();
                }
                setErrMess("");
            }).catch(err => {
                setErrMess(err?.response?.data);
                setSaveAccess(true);
            })
        }else{
            categoryapi.updateEmployee(name, email, password, selectedRoleId, eid).then(res => {
                if(res.status===200){
                    back();
                }
                setErrMess("");
            }).catch(err => {
                setErrMess(err?.response?.data);
                setSaveAccess(true);
            })
        }

    }

    let back = () => {
        setIsNew(false);
        setName("");
        setEmail("");
        setPassword("");
        setUid(false);
        setUpdatePass(false);
        setSavedCats([]);
        setSelectedRoleId(-1);
        setSavedRoleName("");
        setCats([]);
        setEid(-1);
        submitEmployee();
    }

    let handleOpen = (event, id) => {
        setIsNew(true);
        categoryapi.getEmployee(id).then(res => {
            setName(res.data.username);
            setEmail(res.data.email);
            setUid(true);
            setPassword("");
            setSavedRoleName(res.data.roleName);
            setCats(roles[0].categoryDtoList);
            setSelectedRoleId(roles[0].id);
            setEid(id);
            for(let i=0; i<roles.length; i++){
                if(roles[i].name===res.data.roleName){
                    setSavedCats(roles[i].categoryDtoList);
                }
            }
            setSaveAccess(true);
            setErrMess("");
        }).catch(err => {
            setErrMess(err?.response?.data);
        })
    }

    let handleDelete = (event, id) => {
        categoryapi.deleteEmployee(id).then(res => {
            if(res.status===200){
                submitEmployee();
            }
            setErrMess("");
        }).catch(err => {
            setErrMess(err?.response?.data);
        })
    }

    let renderEmployee = (employee) => {
        return (
            <>
                <li className="Messages-list">
                <span
                    className="avatar"
                    style={{ backgroundColor: "yellow" }}
                />
                    <div className="Message-content-1">
                        <div className="username">
                            {employee.roleName}
                        </div>
                        <div className="text">{employee.username}</div>
                    </div>
                    <Button variant="contained" color="primary" onClick={event => handleOpen(event, employee.id)} >
                        Open
                    </Button>
                    <Button variant="contained" color="primary" onClick={event => handleDelete(event, employee.id)} >
                        Delete
                    </Button>
                </li>
            </>
        );
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
                    {!!uid?(
                        <>
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
                        </>

                    ):
                        <>
                            <TextField
                                label="Type password"
                                placeholder="password"
                                onChange={handlePassChange}
                                margin="normal"
                                onKeyPress={event => {
                                    if (event.key === 'Enter') {
                                        handleSubmit();
                                    }
                                }}
                            />
                        </>
                    }
                    <p>{savedRoleName}</p>
                    <ul className="adminFunc">{savedCats.map(cat =><li>{cat.name}</li>)}</ul>
                    <select name={selectedRoleId} onChange={handleChange}>
                        {roles.map(option =>
                            renderOption(option)
                        )}
                    </select>
                    <ul className="adminFunc">{cats.map(cat =><li>{cat.name}</li>)}</ul>
                    {saveAccess &&(
                        <Button onClick={saveEmployee}>
                            Save
                        </Button>
                    )}

                    <Button onClick={back}>
                        Cancel
                    </Button>
                </>
            ):
                <>
                    <Button onClick={openNew}>
                        Create new role
                    </Button>
                    <ul className="chat-list">
                        {employees.map(cat => renderEmployee(cat))}
                    </ul>
                </>
            }
        </>
    )
}
export default Employees