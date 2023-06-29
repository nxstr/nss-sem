import React, {useState} from "react";
import Button from "@material-ui/core/Button";
import Input from "../Input";
import chatAPI from "../../services/chatapi";
import categoryAPI from "../../services/categoryapi";

const Categories = ({cats, user, onSendMessageCat}) => {

    const [uid, setUid] = useState(null);
    const [errMess, setErrMess] = useState("");
    let getUpdateName = (name) => {
        categoryAPI.updateCategory(user.username, uid, name).then(res => {
            if(res.status===200){
                setUid(null);
                onSendMessageCat();
            }
            setErrMess("");
        }).catch(err => {
            setErrMess(err?.response?.data);
        })
    }

    let handleUpdate = (event, id) => {
        setUid(id);

    }



    let handleDelete = (event, id) => {
        categoryAPI.deleteCategory(user.username, id).then(res => {
            if(res.status===200){
                onSendMessageCat();
            }
            setErrMess("");
        }).catch(err => {
            setErrMess(err?.response?.data);
        })

    }


    let onSendMess = (msgText) => {
        categoryAPI.createCategory(user.username, msgText).then(res => {
            if(res.status===201){
                onSendMessageCat();
            }
            setErrMess("");
        }).catch(err => {
            setErrMess(err?.response?.data);
        })
    }

    let renderCategory = (category) => {

        const {id, name} = category;
        return (
            <>
                <li className="Messages-list">
                <span
                    className="avatar"
                    style={{ backgroundColor: "yellow" }}
                />
                    <div className="Message-content-1">
                        <div className="username">
                            {id}
                        </div>
                        <div className="text">{name}</div>
                    </div>
                    <Button variant="contained" color="primary" onClick={event => handleDelete(event, id)} >
                        Delete
                    </Button>
                    <Button variant="contained" color="primary" onClick={event => handleUpdate(event, id)} >
                        Update
                    </Button>
                    {!!uid && uid===id && (
                        <div>
                            <Input onSendMessage={getUpdateName}/>
                        </div>
                    )}
                </li></>

        );
    }

    return (
        <>
            {errMess!=="" && (
                <p>{errMess}</p>
            )}
            <div className="cats">
                <Input onSendMessage={onSendMess}/>
                <ul className="chat-list">
                    {cats.map(cat => renderCategory(cat))}
                </ul>
            </div>
        </>
    )

}

export default Categories