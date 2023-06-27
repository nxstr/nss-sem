import React, {useState} from "react";
import Button from "@material-ui/core/Button";
import chatapi from "../services/chatapi";

const ChatInfo = ({activeChat, categories, isAdmin}) => {
    const [addCat, setAddCat] = useState(false);
    const [savedCats, setSavedCats] = useState([]);
    const [availableCats, setAvailableCats] = useState([]);
    const [selectedCat, setSelectedCat] = useState(null);



    let addCategory = () => {
        console.log(savedCats)
        setAddCat(true);
        let arr = [];
        for(let i=0; i<categories.length; i++){
            let tmp = false;
            for(let j=0; j<savedCats.length; j++){
                if(categories[i]===savedCats[j]){
                    tmp=true;
                    break;
                }
            }
            if(!tmp){
                arr.push(categories[i]);
            }
        }
        setAvailableCats(arr);
        if(arr.length!==0){
            setSelectedCat(arr[0].id.toString());
        }
    }
    let removeCategories = () => {
        setSavedCats([]);
    }

    let handleChange = event => {
        setSelectedCat(event.target.value);
    }

    let addCats = () => {
        setAddCat(false);
        for(let i=0; i<categories.length; i++){
            if(categories[i].id.toString()===selectedCat){
                setSavedCats(savedCats.concat(categories[i]));
            }
        }
    }

    let cancel = () => {
        setAddCat(false);
        setAvailableCats([]);
        setSelectedCat(null);
    }

    let closeChat = () => {
        chatapi.closeChat(activeChat.id).then(res=>{
            if(res.status===200){
                activeChat.open=false;
                activeChat.categories = [];
            }
        }).catch(() => {
            console.log('Error Occured while creating role to api');
        })
    }

    let openChat = () => {
        chatapi.openChat(activeChat.id).then(res=>{
            if(res.status===200){
                activeChat.open=true;
            }
        }).catch(() => {
            console.log('Error Occured while creating role to api');
        })
    }

    let saveCats = () => {
        chatapi.saveCategories(activeChat.id, savedCats).then(res=>{
            if(res.status===200){
                activeChat.categories=savedCats;
                setSavedCats([]);
                setAddCat(false);
            }
        }).catch(() => {
            console.log('Error Occured while creating role to api');
        })
    }

    return (
        <>
            <p>chatName: {activeChat.playerUsername}</p>
            <p>isOpen: {activeChat.open.toString()}</p>
            {activeChat.open===true?(
                <Button onClick={closeChat}>
                    Close Chat
                </Button>
            ):
                <Button onClick={openChat}>
                    Open Chat
                </Button>
            }

            <p>Categories: <ul className="cats1">{activeChat.categories.map(cat =><li>{cat.name}</li>)}</ul></p>
            {isAdmin && (
                <>

            <Button onClick={addCategory}>
                add category
            </Button>

            {savedCats.length!==0 && (

                <>
                    <Button onClick={removeCategories}>
                        clear categories
                    </Button>
                    <p>New categories: <ul className="cats1">{savedCats.map(cat =><li>{cat.name}</li>)}</ul></p>
                </>

            )}
            {addCat &&(
                <>
                    <select name={selectedCat} onChange={handleChange}>
                        {availableCats.map(option =>
                            <option key={option.id} value={option.id}>
                                {option.name}
                            </option>
                        )}
                    </select>
                    <Button onClick={addCats}>
                        add
                    </Button>
                    <Button onClick={cancel}>
                        cancel
                    </Button>
                </>


            )}
            <Button onClick={saveCats}>
                Save Categories
            </Button>
                </>
            )}
        </>
    );
}
export default ChatInfo