import Button from "@material-ui/core/Button";
import React, {useState} from "react";
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import Categories from "./Categories";
import categoryAPI from "../services/categoryapi";
import Roles from "./Roles";
import Employees from "./Employees";
import Players from "./Players";

const AdminPanel =({user}) => {
    const [cats, setCats] = useState([]);
    const [roles, setRoles] = useState([]);
    const [emps, setEmps] = useState([]);
    const [showNum, setShowNum] = useState(0);
    const [players, setPlayers] = useState([]);
    let getCats = () =>{
        categoryAPI.getCats(user.username).then(res=> {
            setCats(res.data);
        }).catch(() => {
            console.log('Error Occured while getting cats to api');
        })

    }

    let getRoles = () =>{
        categoryAPI.getRoles().then(res=> {
            setRoles(res.data);
        }).catch(() => {
            console.log('Error Occured while getting roles to api');
        })
    }

    let getEmps = () => {
        categoryAPI.getEmps().then(res=> {
            setEmps(res.data);
        }).catch(() => {
            console.log('Error Occured while getting roles to api');
        })
    }

    let getPlayers = () => {
        categoryAPI.getPlayers().then(res =>{
            setPlayers(res.data);
        }).catch(() => {
            console.log('Error Occured while getting roles to api');
        })
    }

    let loadCats = () =>{
        getCats();
        setShowNum(1);
    }

    let loadRoles = () =>{
        getRoles();
        getCats();
        setShowNum(2);
    }

    let loadEmps = () => {
        getRoles();
        getEmps();
        setShowNum(3);
    }

    let loadPlayers = () => {
        getPlayers();
        setShowNum(4);
    }

    let onSendMessageCat = () => {
        categoryAPI.getCats(user.username).then(res => {
            setCats(res.data);
            console.log(res.data);
            return (<Categories cats={cats}
                                user={user}
                                onSendMessageCat={onSendMessageCat}/>);
        }).catch(() => {
            console.log('Error Occured while sending message to api');
        })
    }

    let submit = () => {
        categoryAPI.getRoles().then(res=> {
            setRoles(res.data);
            return (<Roles roles={roles} categories={cats} submit={submit}/>);
        }).catch(() => {
            console.log('Error Occured while getting roles to api');
        })
    }

    let submitEmployee = () => {
        categoryAPI.getEmps().then(res=> {
            setEmps(res.data);
            return (<Employees employees={emps} roles={roles} submitEmployee={submitEmployee}/>);
        }).catch(() => {
            console.log('Error Occured while getting roles to api');
        })
    }

    let submitPlayer = () => {
        categoryAPI.getPlayers().then(res =>{
            setPlayers(res.data);
            return(<Players players={players} submitPlayer={submitPlayer}/>);
        }).catch(() => {
            console.log('Error Occured while getting roles to api');
        })
    }

    return (
        <div className="chat">
            <ul className="adminFunc">
                <li>
                    <Button onClick={loadCats} className="CatsButton">
                        Categories
                    </Button>
                </li>
                <li>
                    <Button onClick={loadRoles} className="CatsButton">
                        Roles
                    </Button>
                </li>
                <li>
                    <Button onClick={loadEmps} className="CatsButton">
                        Employees
                    </Button>
                </li>
                <li>
                    <Button onClick={loadPlayers} className="CatsButton">
                        Players
                    </Button>
                </li>
            </ul>
            {showNum===1 && (
                <Categories cats = {cats} user={user} onSendMessageCat={onSendMessageCat}/>
            )}
            {showNum===2 && (
                    <Roles roles={roles} categories={cats} submit={submit}/>
            )}
            {showNum===3 && (
               <Employees employees={emps} roles={roles} submitEmployee={submitEmployee}/>
            )}
            {showNum===4 && (
                <Players players={players} submitPlayer={submitPlayer}/>
            )}
        </div>
    )
}

export default AdminPanel