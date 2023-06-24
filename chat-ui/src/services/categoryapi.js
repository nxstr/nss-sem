import Axios from "axios";

const api = Axios.create({
    baseURL: '/api/',
});

const categoryAPI = {
    createCategory: (username, name) =>{
        let msg = {
            name: name
        }
        return api.post(`categories/new`, msg);
    },
    getCats: (username) => {
        return api.get(`categories`);
    },
    deleteCategory:(username, id) => {
        return api.delete(`categories/delete/${id}`);
    },
    updateCategory:(username, id, name) => {
        let msg = {
            id: id,
            name: name
        }
        return api.put(`categories/update/${id}`, msg);
    },
    login:(username, password) => {
        let msg = {
            username: username,
            password:password
        }
        console.log(password, "password")
        return api.post(`login`, msg);
    },
    getRoles:()=>{
        return api.get(`roles`);
    },
    createRole:(name, parentId, categoryDtoList) => {
        let dto = {
            "name": name,
            "categoryDtoList": categoryDtoList,
            "parentId" : parentId
        }
        return api.post(`role/new`, dto);
    },
    getRole:(id) => {
        return api.get(`roles/get/${id}`);
    },
    deleteRole:(id) => {
        return api.delete(`role/delete/${id}`);
    },
    updateRole:(name, parentId, categoryDtoList, id) => {
        let dto = {
            "name": name,
            "categoryDtoList": categoryDtoList,
            "parentId" : parentId,
            "id":id
        }
        return api.put(`role/edit`, dto);
    },
    createEmployee:(username, email, password, roleId) => {
        let dto = {
            "username": username,
            "password": password,
            "email": email,
            "roleId":roleId
        }
        return api.post(`employee/new`, dto);
    },
    getEmps:() => {
        return api.get(`employee/getAll`);
    },
    deleteEmployee:(id) =>{
        return api.delete(`emaployee/delete/${id}`);
    },
    getEmployee:(id) => {
        return api.get(`employee/get/${id}`);
    },
    updateEmployee:(username, email, password, roleId, id) => {
        let dto = {
            "username": username,
            "password": password,
            "email": email,
            "roleId":roleId,
            "id": id
        }
        return api.put(`employee/update/${id}`, dto);
    }

}

export default categoryAPI;