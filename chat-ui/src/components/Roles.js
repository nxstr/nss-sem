import Input from "./Input";
import React, {useState} from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import categoryapi from "../services/categoryapi";

const Roles =({categories, roles, submit}) => {
    const [isNew, setIsNew] = useState(null);
    const [name, setName] = useState("");
    const [selected, setSelected] = useState(-1);

    const [cats, setCats] = useState([]);
    const [showCats, setShowCats] = useState(false);
    const [catsForAdd, setCatsForAdd] = useState([]);
    const [render, setRender] = useState(false);
    const [selectedCat, setSelectedCat] = useState(-1);

    const [addedCats, setAddedCats] = useState([]);
    const [selectedParentName, setSelectedParentName] = useState("");
    const [savedCats, setSavedCats] = useState([]);
    const [id, setId] = useState(-1);


    let showRoleCategories = (id) => {
        console.log("role id: ", id, typeof id)
        for(let i=0; i<roles.length; i++){

            console.log("roles id: ", roles[i].id, typeof roles[i].id)
            if(roles[i].id.toString()===id){

                setCats(roles[i].categoryDtoList);
                setAddedCats([]);
                console.log(roles[i].categoryDtoList);
            }
        }
    }

    const handleChange = event => {
        console.log(event.target.value);
        setSelected(event.target.value);
        console.log(event.target.value)
        setRender(true);
        showRoleCategories(event.target.value);
    };


    let handleNameChange = event => setName(event.target.value);

    let openNew = () =>{
        setIsNew(true);
        if(roles.length!==0){
            setSelected(roles[0].id);
            setSelectedParentName("");
            setSavedCats([]);
            setCats(roles[0].categoryDtoList);
            setRender(true);
        }
    }

    let handleSubmit = () => {
        console.log("enter");
    }

    let handleOpen = (event, id) => {
        setIsNew(true);
        categoryapi.getRole(id).then(res => {
            setRender(true);
            console.log(res);
            setId(res.data.id);
            setName(res.data.name);
            setSavedCats(res.data.categoryDtoList);
            if(id!==roles[0].id){
                setSelected(roles[0].id);
                setCats(roles[0].categoryDtoList);
            }else{
                setSelected(roles[1].id);
                setCats(roles[1].categoryDtoList);
            }
            // setSelected(res.data.parentId);
            let arr = [];
            let tmp = false;
            for(let i=0; i<roles.length; i++){
                if(roles[i].id.toString()===res.data.parentId.toString()){
                    setSelectedParentName(roles[i].name);
            //         for(let k=0; k<res.data.categoryDtoList; k++) {
            //             for (let j = 0; j < roles[i].categoryDtoList.length; j++) {
            //                 if (roles[i].categoryDtoList[j] === res.data.categoryDtoList[k]){
            //                     tmp = true;
            //                     break;
            //                 }
            //             }
            //             if(!tmp){
            //                 arr.push(res.data.categoryDtoList[k]);
            //             }
            //         }
                }
            //
            }
            // setAddedCats(arr);

        }).catch(() => {
            console.log('Error Occured while creating role to api');
        })
    }

    let handleDelete = (event, id) => {
        categoryapi.deleteRole(id).then(res => {
            if(res.status===200){
                submit();
            }
        }).catch(() => {
            console.log('Error Occured while creating role to api');
        })
    }

    let renderRole = (role) => {

        const {id, name, categoryDtoList} = role;
        if(name!=="admin"){
            return (
                <li className="Messages-list">
                <span
                    className="avatar"
                    style={{ backgroundColor: "yellow" }}
                />
                    <div className="Message-content">
                        <div className="username">
                            {name}
                        </div>
                        <div className="text">{id}</div>
                    </div>
                    <Button variant="contained" color="primary" onClick={event => handleOpen(event, id)} >
                        Open
                    </Button>
                    <Button variant="contained" color="primary" onClick={event => handleDelete(event, id)} >
                        Delete
                    </Button>
                </li>
            );
        }
    }

    let handleAddCats = () => {

        let arr = [];
        for(let i=0; i<categories.length; i++){
            let notAdd = false;
            for(let j=0; j<cats.length; j++){
                if(categories[i].id===cats[j].id){
                    notAdd = true;
                    break;
                }
            }
            if(!notAdd){
                arr.push(categories[i]);
            }
        }
        setCatsForAdd(arr);
        if(arr.length!==0){
            setSelectedCat(arr[0].id.toString());
            setShowCats(true);
        }

    }

    let handleChangeCat = event => {
        console.log(event.target.value);
        setSelectedCat(event.target.value);
    }

    let addCategory = () => {
        setRender(false);
        console.log("here", selectedCat)
        console.log(selectedCat, typeof selectedCat)
        for(let i=0; i<categories.length; i++){
            if(categories[i].id.toString()===selectedCat){
                setCats(cats.concat(categories[i]));
                setAddedCats(addedCats.concat(categories[i]));
                console.log("here");
            }
        }
        setRender(true);
        setShowCats(false);
    }

    let cancelCategory = () => {
        setSelectedCat(-1);
        setShowCats(false);
        setCatsForAdd([]);
    }

    let back = () => {
        setIsNew(false);
        setSelectedCat(-1);
        setShowCats(false);
        setCatsForAdd([]);
        setCats([]);
        setAddedCats([]);
        setName("");
        setSelected(roles[0].id);
        setSelectedParentName("");
        setRender(false);
        setId(-1);
        setSavedCats([]);
        submit();
    }

    let saveRole = () => {
        if(id===-1){
            categoryapi.createRole(name, selected, addedCats).then(res => {
                console.log(res);
                if(res.status===200){
                    back();
                }
            }).catch(() => {
                console.log('Error Occured while creating role to api');
            })
        }else{
            categoryapi.updateRole(name, selected, addedCats, id).then(res => {
                console.log(res);
                if(res.status===200){
                    back();
                }
            }).catch(() => {
                console.log('Error Occured while creating role to api');
            })
        }

    }

    let renderOption = (option) => {
        if(option.name!=="admin") {
            if (id !== -1) {
                if (id !== option.id) {
                    return (
                        <option key={option.id} value={option.id}>
                            {option.name}
                        </option>
                    );
                }
            } else {
                return (
                    <option key={option.id} value={option.id}>
                        {option.name}
                    </option>
                );
            }
        }
    }

    return (
        <>
            {!!isNew?(
                <>
                    <TextField
                        label="Type role name"
                        value={name}
                        placeholder="name"
                        onChange={handleNameChange}
                        margin="normal"
                        onKeyPress={event => {
                            if (event.key === 'Enter') {
                                handleSubmit();
                            }
                        }}
                    />
                    <p>{selectedParentName}</p>
                    <ul className="adminFunc">{savedCats.map(cat =><li>{cat.name}</li>)}</ul>
                    <select name={selected} onChange={handleChange}>
                        {roles.map(option =>
                            renderOption(option)
                        )}
                        <option key={null} value={null}>
                            {null}
                        </option>
                    </select>
                    {render && (
                        <ul className="adminFunc">{cats.map(cat =><li>{cat.name}</li>)}</ul>
                    )}
                    <Button  onClick={event => handleAddCats(event)}>
                        add categories
                    </Button>
                    {showCats &&(
                        <>
                            <select name={selectedCat} onChange={handleChangeCat}>
                                {catsForAdd.map(option => (
                                    <option key={option.id} value={option.id}>
                                        {option.name}
                                    </option>
                                ))}
                            </select>
                            <Button onClick={addCategory}>
                                Add
                            </Button>
                            <Button onClick={cancelCategory}>
                                Cancel
                            </Button>
                        </>

                    )}
                    <Button onClick={saveRole}>
                        Save
                    </Button>
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
                        {roles.map(cat => renderRole(cat))}
                    </ul>
                </>
            }

        </>
    )
}
export default Roles