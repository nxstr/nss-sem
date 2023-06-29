import React, {useState} from 'react';
import SockJsClient from 'react-stomp';
import './App.css';

import chatAPI from './services/chatapi';
import {randomColor} from './utils/common';

import categoryAPI from "./services/categoryapi";
import Button from "@material-ui/core/Button";
import {BrowserRouter as Router, Navigate, Route, Routes,} from 'react-router-dom';
import RegForm from "./components/RegForm";
import LoginForm from "./components/LoginForm";
import AllChats from "./components/AllChats";
import Input from "./components/Input";
import Messages from "./components/Messages";
import AdminPanel from "./components/AdminPanel";
import Account from "./components/Account";
import CategorySelect from "./components/CategorySelect";
import ChatInfo from "./components/ChatInfo";


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
  const [chatInfo, setChatInfo] = useState(null);
  const [errMess, setErrMess] = useState("");
  let path = "/home"


  let onConnected = () => {
    console.log("Connected!!")
    chatAPI.loginMessage(user.username).then(res => {
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
    })
    getChats(user);
  }

  let getChats = (user) =>{
    chatAPI.getChats(user.username).then(res=> {
      res.data.sort((a,b) => Date.parse(b.lastMessage.date) - Date.parse(a.lastMessage.date));
      res.data.sort((a, b)=> Number(b.open)-Number(a.open));
        setChats(res.data)
      console.log(res.data);
      // setChats(chats => res.data)
      let hasChatObj = false;
      for(let i=0; i<res.data.length; i++){
        if(res.data[i]!==null){
          hasChatObj = true;
          break;
        }
      }
      if(!hasChatObj){
        setHasChat(false);
      }else{
        setHasChat(true);
      }
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
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
    // msg.date = Date.parse(msg.date);
    console.log('New Message Received!!', msg);
    if(msg.messageType==="message"){
      if(activeChat!=null && msg.chat===activeChat.chatName){
        setMessages(messages.concat(msg));
        for(let i=0; i<chats.length; i++){
          if(chats[i].playerUsername===activeChat.chatName){
            chats[i].lastMessage = msg;
          }
        }
        chats.sort((a,b) => Date.parse(b.lastMessage.date) - Date.parse(a.lastMessage.date));
        chats.sort((a, b)=> Number(b.open)-Number(a.open));
        console.log(chats);
        setChats([].concat(chats))
      }else if(activeChat===null || msg.chat!==activeChat.chatName){
        for(let i=0; i<chats.length; i++){
          if(chats[i].playerUsername===msg.chat){
            chats[i].lastMessage = msg;
          }
        }
        chats.sort((a,b) => Date.parse(b.lastMessage.date) - Date.parse(a.lastMessage.date));
        chats.sort((a, b)=> Number(b.open)-Number(a.open));
        console.log(chats);
        setChats([].concat(chats))
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
      if(user.type==="admin"){
        setEmp(true);
      }
    }else if(msg.messageType==="forceLogout"){
      logout();
    }

  }

  let onSendMessage = (msgText) => {
    let arr = [];
    for(let i=0; i<selectedOptions.length; i++){
      for(let j=0; j<cats.length; j++){

        if(selectedOptions[i].toString()===cats[j].id.toString()){
          arr.push(cats[j]);
        }
      }
    }
    chatAPI.sendMessage(user.username, msgText, activeChat, arr).then(res => {
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
    })
  }


  let categoryCreate = () => {
    categoryAPI.createCategory(user.username).then(res => {
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
    })
  }

  const login = async (username, password) => {
    categoryAPI.login(username, password).then(res =>{
      console.log(res);
      if(res.status===200){
        setUser({
          username: username,
          color: randomColor(),
          type: "player"
        })
        getChats(user);
        setErrMess("");
      }
    }).catch(err => {
      setErrMess(err?.response?.data);
    })

  };

  let handleLoginSubmit = (username, password) => {
    login(username, password);
  }

  let handleReg = () => {
    setIsReg(true);
  }

  let getCats = () => {
    categoryAPI.getCats(user.username).then(res => {
      setCats(res.data);
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
    })
  }


  let handleChatId = (id, name) =>{

    setActiveChat({
      chatId: id,
      chatName: name
    })

    if(user.type==="player"){
      getCats();
    }else{
      let ch = null;
      for(let i=0; i<chats.length; i++){
        if(chats[i].id.toString()===id.toString()){
          ch = chats[i];
        }
      }
      setChatInfo(ch);
      getCats();
      console.log(ch);
    }
    setShowAcc(false);
    setIsShown(false);
    // window.location.replace(`http://localhost:3000/allChats/${id}`)
    chatAPI.getMessages(id, user.username).then(res =>{
      setMessages([].concat(res.data))
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
    })
  }


  let handleChatIdFromMess = (id, name) =>{

    setActiveChat({
      chatId: id,
      chatName: name
    })
    if(user.type==="player"){
      getCats();
    }else{
      let ch = null;
      for(let i=0; i<chats.length; i++){
        if(chats[i].id.toString()===id.toString()){
          ch = chats[id];
        }
      }
      setChatInfo(ch);
      getCats();
    }
    setIsShown(false);
    setShowAcc(false);
    chatAPI.getMessages(id, user.username).then(res =>{
      setMessages([].concat(res.data))
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
    })
  }

  let getChat = (user) => {
    if (user) {
      return user.username
    } return ""
  }

  let loadCats = () =>{
    // getCats();
    setIsShown(true);
    setShowAcc(false);
    setActiveChat(null);
    setChatInfo(null);
    path = "/home/cats";
  }

  let createChat = () => {
    chatAPI.createChat().then(res => {
      if(res.status===200){
        getChats(user);
      }
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
    })
  }

  let handleRegSubmit = (username, password, email) => {
    chatAPI.registerPlayer(username, password, email).then(() => {
      setIsReg(false);
      setHasChat(false);
      setShowAcc(false);
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
    })
  }

  let logout = () => {
    chatAPI.logout().then(() => {
      setUser(null);
      setActiveChat(null);
      setChatInfo(null);
      setChats([]);
      setHasChat(false);
      setMessages([])
      setEmp(false);
      setShowAcc(false);
      setIsShown(false);
      setErrMess("");
    }).catch(err => {
      setErrMess(err?.response?.data);
    })
  }

  let loadCurrent = () => {
    setShowAcc(true);
    setIsShown(false);
    setActiveChat(null);
    setChatInfo(null);
    if(user.type!=="player"){
      chatAPI.getCurrentEmployee().then(res => {
        setCurrentAcc(res.data);
        setErrMess("");
      }).catch(err => {
        setErrMess(err?.response?.data);
      })
    }else{
      chatAPI.getCurrentPlayer().then(res => {
        setCurrentAcc(res.data);
        setErrMess("");
      }).catch(err => {
        setErrMess(err?.response?.data);
      })
    }
  }

  let submitCats = (value) => {
    setSelectedOptions(value);
  }

  return (
    <div className="App">
      {errMess!=="" && (
          <p>{errMess}</p>
      )}
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
                                <>
                                  <div className="chat">

                                    <Messages
                                        messages={messages}
                                        currentUser={user}
                                        chats={chats}
                                        onSubmitChat = {handleChatIdFromMess}
                                    />
                                    {user.type==="player" &&(
                                        <CategorySelect categories={cats} submitCats={submitCats}/>
                                    )
                                    }
                                    <Input onSendMessage={onSendMessage}
                                           categoryCreate={categoryCreate}/>
                                  </div>
                                  {user.type !== "player" && (
                                      <div className="chatInfo">
                                          <ChatInfo activeChat={chatInfo} categories={cats} isAdmin={isEmp}/>
                                      </div>
                                  )
                                  }
                                </>
                                } />
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
