import React, { useState } from 'react';
import SockJsClient from 'react-stomp';
import './App.css';

import chatAPI from './services/chatapi';
import { randomColor } from './utils/common';

import categoryAPI from "./services/categoryapi";
import Button from "@material-ui/core/Button";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate, useLocation,
} from 'react-router-dom';
import RegForm from "./components/RegForm";
import LoginForm from "./components/LoginForm";
import AllChats from "./components/AllChats";
import Input from "./components/Input";
import Messages from "./components/Messages";
import Categories from "./components/Categories";
import AdminPanel from "./components/AdminPanel";
import Account from "./components/Account";
import CategorySelect from "./components/CategorySelect";



const SOCKET_URL = 'http://localhost:8080/ws-chat/';

const App = () => {

  const [messages, setMessages] = useState([])
  const [chats, setChats] = useState([])
  const [user, setUser] = useState(null)
  const [activeChat, setActiveChat] = useState(null)
  const [isEmp, setEmp] = useState(false);
  const [cats, setCats] = useState([]);
  const [isShown, setIsShown] = useState(false);
  const [hasChat, setHasChat] = useState(false);
  const [isReg, setIsReg] = useState(false);
  const [showAcc, setShowAcc] = useState(false);
  const [currentAcc, setCurrentAcc] = useState([]);
  const [isPlayer, setPlayer] = useState(true);
  const [selectedOptions, setSelectedOptions] = useState([]);
  let path = "/home"


  let onConnected = () => {
    console.log("Connected!!")
    console.log(user.username, " username")
    chatAPI.loginMessage(user.username).then(res => {
      console.log('Sent login', res);
    }).catch(err => {
      console.log('Error Occured while sending message to api', err);
    })
    getChats(user);
  }

  let getChats = (user) =>{
    chatAPI.getChats(user.username).then(res=> {
        setChats(res.data)
      console.log(res.data)
      // setChats(chats => res.data)
      let hasChatObj = false;
      for(let i=0; i<res.data.length; i++){
        if(res.data[i]!==null){
          hasChatObj = true;
          break;
        }
      }
      if(!hasChatObj){
        console.log("len is 0", res.data.length, res.data)
        setHasChat(false);
      }else{
        console.log("len is not 0 " , res.data.length, res.data)
        setHasChat(true);
      }
      console.log("all chats ",chats)
    }).catch(() => {
      console.log('Error Occurred while getting chats to api');
    })

  }

  // let onSendMessageCat = () => {
  //       categoryAPI.getCats(user.username).then(res => {
  //         setCats(res.data);
  //         return (<Categories cats={cats}
  //                             user={user}
  //                             onSendMessageCat={onSendMessageCat}/>);
  //       }).catch(() => {
  //     console.log('Error Occured while sending message to api');
  //   })
  // }

  // let getCats = () =>{
  //   categoryAPI.getCats(user.username).then(res=> {
  //     setCats(res.data);
  //     console.log("all cats ",res.data)
  //   }).catch(() => {
  //     console.log('Error Occured while getting cats to api');
  //   })
  //
  // }

  let onMessageReceived = (msg) => {
    console.log('New Message Received!!', msg);
    if(msg.messageType==="message"){
      console.log(msg.chat.id, " active chatttttt")
      if(activeChat!=null && msg.chat===activeChat.chatName){
        setMessages(messages.concat(msg));
        // let chatsCopy = chats;
        for(let i=0; i<chats.length; i++){
          console.log("chatsssss: ", chats)
          if(chats[i].playerUsername===activeChat.chatName){
            chats[i].lastMessage = msg;
            console.log(chats[i])
          }
        }
        setChats([].concat(chats))
        console.log(messages, " messages")
      }
    }else if(msg.messageType==="chatListUpdate"){
      getChats(user);
    }else if(msg.messageType==="login"){
      user.type = msg.content;
      if(user.type!=="player"){
        setPlayer(false);
      }else{
        setPlayer(true);
      }
      console.log(user.type, "typeeeeeeeeeeeeee")
      if(user.type==="admin"){
        setEmp(true);
      }
    }

  }

  let onSendMessage = (msgText) => {
    let arr = [];
    console.log(selectedOptions, "aaaaaaaaaaaaa");
    for(let i=0; i<selectedOptions.length; i++){
      for(let j=0; j<cats.length; j++){
        if(selectedOptions[i].toString()===cats[j].id.toString()){
          arr.push(cats[i]);
        }
      }
    }
    console.log(arr);
    chatAPI.sendMessage(user.username, msgText, activeChat, arr).then(res => {
      console.log('Sent', res);
    }).catch(() => {
      console.log('Error Occured while sending message to api');
    })
  }


  let categoryCreate = () => {
    categoryAPI.createCategory(user.username).then(res => {
      console.log('Cat', res);
    }).catch(() => {
      console.log('Error Occured while creating category');
    })
  }

  const login = async (username, password) => {
    categoryAPI.login(username, password).then(res =>{
      console.log(res, "=================================")
      if(res.status===200){
        setUser({
          username: username,
          color: randomColor(),
          type: "player"
        })
        getChats(user)
      }else{
        console.log("FORBIDDEN")
      }
    }).catch(() => {
      console.log('Error Occured while getting messages to api');
    })

  };

  let handleLoginSubmit = (username, password) => {
    login(username, password).then(res => {
      console.log('Sent loginnnnnnnnnnnnnnnnnnnnn', res, " ", hasChat);

    }).catch(err => {
      console.log('Error Occured while sending message to api', err);
    });
  }

  let handleReg = () => {
    setIsReg(true);
  }

  let getCats = () => {
    categoryAPI.getCats(user.username).then(res => {
      setCats(res.data);
    }).catch(err=>{
      console.log('Error Occured while sending message to api', err);
    });
  }


  let handleChatId = (id, name) =>{

    setActiveChat({
      chatId: id,
      chatName: name
    })
    if(user.type==="player"){
      getCats();
    }
    setShowAcc(false);
    setIsShown(false);
    // window.location.replace(`http://localhost:3000/allChats/${id}`)
    chatAPI.getMessages(id, user.username).then(res =>{
      console.log(res.data)
      setMessages([].concat(res.data))
    }).catch(() => {
      console.log('Error Occured while getting messages to api');
    })
  }


  let handleChatIdFromMess = (id, name) =>{

    setActiveChat({
      chatId: id,
      chatName: name
    })
    if(user.type==="player"){
      getCats();
    }
    setIsShown(false);
    setShowAcc(false);
    chatAPI.getMessages(id, user.username).then(res =>{
      console.log(res.data)
      setMessages([].concat(res.data))
    }).catch(() => {
      console.log('Error Occured while getting messages to api');
    })
  }

  let getChat = (user) => {
    if (user) {
      console.log(" user is " + user.username)
      return user.username
    } return ""
  }

  let loadCats = () =>{
    // getCats();
    setIsShown(true);
    setShowAcc(false);
    setActiveChat(null);
    path = "/home/cats";
  }

  let createChat = () => {
    chatAPI.createChat().then(res => {
      if(res.status===200){
        getChats(user);
      }
    }).catch(() => {
      console.log('Error Occured while getting messages to api');
    })
  }

  let handleRegSubmit = (username, password, email) => {
    chatAPI.registerPlayer(username, password, email).then(() => {
      setIsReg(false);
      setHasChat(false);
      setShowAcc(false);
    }).catch(() => {
      console.log('Error Occured while getting messages to api');
    })
  }

  let logout = () => {
    chatAPI.logout().then(() => {
      setUser(null);
      setActiveChat(null);
      setChats([]);
      setHasChat(false);
      setMessages([])
      setEmp(false);
      setShowAcc(false);
      console.log("logout")
    }).catch(() => {
      console.log('Error Occured while getting messages to api');
    })
  }

  let loadCurrent = () => {
    setShowAcc(true);
    setIsShown(false);
    if(user.type!=="player"){
      chatAPI.getCurrentEmployee().then(res => {
        setCurrentAcc(res.data);
        console.log(res.data.username);
      }).catch(() => {
        console.log('Error Occured while getting messages to api');
      })
    }
  }

  let submitCats = (value) => {
    setSelectedOptions(value);
  }

  return (
    <div className="App">
      {!!user ?
        (

          <>
            <SockJsClient
              url={SOCKET_URL}
              topics={[`/topic/group/${getChat(user)}`]}
              onConnect={onConnected}
              onDisconnect={console.log("Disconnected!")}
              onMessage={
                msg => onMessageReceived(msg)
              }
              debug={false}

            />
            <ul className="mainButton">
              <li>
                <Button onClick={logout} >
                  Logout
                </Button>
              </li>
              {!!isEmp &&(
                  <li>
                    <Button onClick={loadCats}>
                      Admin Panel
                    </Button>
                  </li>
              )}
              <li>
                <Button onClick={loadCurrent}>
                  Account
                </Button>
              </li>
            </ul>


        <div className="main">
            {!!hasChat?(
                <>
                  <Router>
                    <Routes>
                      <Route path="/" element={<AllChats chats = {chats}
                                                         currentUser = {user}
                                                         onSubmitChat = {handleChatId}/>} />
                      <Route path="/" element={<Navigate replace to="/" />} />
                    </Routes>
                  </Router>
                  {!!activeChat &&(
                          <Router>
                            <Routes>
                              <Route exact path="/" element={
                                <div className="chat">

                                  <Messages
                                      messages={messages}
                                      currentUser={user}
                                      chats={chats}
                                      onSubmitChat = {handleChatIdFromMess}
                                  />
                                  {user.type==="player" &&(
                                      <CategorySelect categories={cats} submitCats={submitCats}/>
                                      )}
                                  <Input onSendMessage={onSendMessage}
                                         categoryCreate={categoryCreate}/>
                                </div>} />
                              <Route path="/" element={<Navigate replace to={{
                                pathname: `/`
                              }} />} />
                            </Routes>
                          </Router>
                      )}
                </>
            ) :
                <>
                  {isPlayer && (
                      <Button onClick={createChat}>
                        Create Chat
                      </Button>
                  )}
                </>


            }
                  {isShown && (
                      <Router>
                        <Routes>
                          <Route path="/" element={<AdminPanel user = {user}/>} />
                          <Route path="/" element={<Navigate replace to="/" />} />
                        </Routes>
                      </Router>
                  )}
          {showAcc && (
              <Account currentAcc={currentAcc} type = {user.type}/>
          )}
          </div>
            </>
        ) :
          <>
            {!!isReg?(
                    <RegForm onSubmit = {handleRegSubmit} />
                ):
                <LoginForm onSubmit={handleLoginSubmit} onSubmitReg={handleReg} />
            }
          </>
      }
    </div>
  )
}

export default App;
