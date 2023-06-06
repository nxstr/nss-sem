import React, { useState } from 'react';
import SockJsClient from 'react-stomp';
import './App.css';
import Input from './components/Input/Input';
import LoginForm from './components/LoginForm';
import Messages from './components/Messages/Messages';
import chatAPI from './services/chatapi';
import { randomColor } from './utils/common';
import AllChats from "./components/AllChats";
import categoryAPI from "./services/categoryapi";
import Button from "@material-ui/core/Button";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from 'react-router-dom';
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
  const [path, setPath] = useState(null);


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
      setChats(chats.concat(res.data));
      console.log("all chats ",res.data)
    }).catch(err => {
      console.log('Error Occured while getting chats to api');
    })

  }

  let getCats = () =>{
    categoryAPI.getCats(user.username).then(res=> {
      setCats(res.data);
      console.log("all cats ",res.data)
    }).catch(err => {
      console.log('Error Occured while getting cats to api');
    })

  }

  let onMessageReceived = (msg) => {
    console.log('New Message Received!!', msg);
    if(msg.messageType==="message"){
      setMessages(messages.concat(msg));
      console.log(messages, " messages")
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
    }).catch(err => {
      console.log('Error Occured while sending message to api');
    })
  }


  let categoryCreate = () => {
    categoryAPI.createCategory(user.username).then(res => {
      console.log('Cat', res);
    }).catch(err => {
      console.log('Error Occured while creating category');
    })
  }
  
    let onLoginMessage = (username) => {
    chatAPI.loginMessage(user.username).then(res => {
      console.log('Sent login', res);
    }).catch(err => {
      console.log('Error Occured while sending message to api');
    })
  }

  let handleLoginSubmit = (users) => {
    var pieces = users.split(" ");
    console.log(pieces[0], " Logged in..");

    setUser({
      username: pieces[0],
      color: randomColor(),
      type: "player"
    })

  }

  let handleChatId = (id) =>{

    setActiveChat({
      chatId: id
    })
    setIsShown(false);
    // window.location.replace(`http://localhost:3000/allChats/${id}`)
    chatAPI.getMessages(id, user.username).then(res =>{
      console.log(res.data)
      setMessages(messages.concat(res.data))
    }).catch(err => {
      console.log('Error Occured while getting messages to api');
    })
  }


  let handleChatIdFromMess = (id) =>{

    setActiveChat({
      chatId: id
    })
    setIsShown(false);
    // window.location.replace(`http://localhost:3000/allChats/${id}`)
    chatAPI.getMessages(id, user.username).then(res =>{
      console.log(res.data)
      setMessages(res.data)
    }).catch(err => {
      console.log('Error Occured while getting messages to api');
    })
  }

  let getChat = (user) => {
    if (user) {
      console.log(" user is " + user.username)
      return user.username
    } return ""
  }


  let getCatsClick = () => {
    categoryAPI.redirect().then(res =>{
      console.log(res.data)

    }).catch(err => {
      console.log('Error Occured while getting messages to api');
    })
    console.log('user', user);
  }

  let loadCats = () =>{
    getCats();
    setIsShown(true);
    setActiveChat(null);
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
            {!!activeChat?(
                <Router>
                  <Routes>
                    <Route exact path="/home" element={
                      <>
                        <Messages
                        messages={messages}
                        currentUser={user}
                        chats={chats}
                        onSubmitChat = {handleChatIdFromMess}
                    />
                      <Input onSendMessage={onSendMessage}
                      categoryCreate={categoryCreate}/>
                      </>} />
                    <Route path="/home" element={<Navigate replace to={{
                      pathname: `/home`
                    }} />} />
                  </Routes>
                </Router>
            ) :

                <Router>
                  <Routes>
                    <Route path="/home" element={<AllChats chats = {chats}
                                                           currentUser = {user}
                                                           onSubmitChat = {handleChatId}/>} />
                    <Route path="/" element={<Navigate replace to="/home" />} />
                  </Routes>
                </Router>
            }

          {/*  if user.type==employee
          show category button*/}
            {!!isEmp?(
                <>
                  <Button onClick={loadCats}>
                    Categories
                  </Button>

                  {isShown && (
                      <Router>
                        <Routes>
                          <Route path="/home/cats" element={<Categories cats = {cats}
                                                                        user={user}/>} />
                          <Route path="/home" element={<Navigate replace to="/home/cats" />} />
                        </Routes>
                      </Router>
                  )}
                </>
            ):
                <></>
            }
          </>
        ) :
        <LoginForm onSubmit={handleLoginSubmit} />
      }
    </div>
  )
}

export default App;
