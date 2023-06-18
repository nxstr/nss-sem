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

  let onSendMessageCat = () => {
        categoryAPI.getCats(user.username).then(res => {
          setCats(res.data);
          return (<Categories cats={cats}
                              user={user}
                              onSendMessageCat={onSendMessageCat}/>);
        }).catch(() => {
      console.log('Error Occured while sending message to api');
    })
  }

  let getCats = () =>{
    categoryAPI.getCats(user.username).then(res=> {
      setCats(res.data);
      console.log("all cats ",res.data)
    }).catch(() => {
      console.log('Error Occured while getting cats to api');
    })

  }

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
      console.log(user.type, "typeeeeeeeeeeeeee")
      if(user.type==="employee"){
        setEmp(true);
      }
    }

  }

  let onSendMessage = (msgText) => {
    chatAPI.sendMessage(user.username, msgText, activeChat).then(res => {
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



  let handleChatId = (id, name) =>{

    setActiveChat({
      chatId: id,
      chatName: name
    })
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
    setIsShown(false);
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
    getCats();
    setIsShown(true);
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
      console.log("logout")
    }).catch(() => {
      console.log('Error Occured while getting messages to api');
    })
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
            <Button onClick={logout}>
              Logout
            </Button>
            {!!isEmp &&(
                <>
                  <Button onClick={loadCats} className="CatsButton">
                    Categories
                  </Button>
                  </>
                )}
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
                <Button onClick={createChat}>
                  Create Chat
                </Button>
            }
            {/*{!!isEmp &&(*/}
            {/*    <>*/}
            {/*      <Button onClick={loadCats} className="CatsButton">*/}
            {/*        Categories*/}
            {/*      </Button>*/}

                  {isShown && (
                      <Router>
                        <Routes>
                          <Route path="/" element={<Categories cats = {cats}
                                                                        user={user}
                          onSendMessageCat={onSendMessageCat}/>} />
                          <Route path="/" element={<Navigate replace to="/" />} />
                        </Routes>
                      </Router>
                  )}
            {/*    </>*/}
            {/*)*/}
            {/*}*/}
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
