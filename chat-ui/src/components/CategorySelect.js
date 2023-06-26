import React, {useState} from "react";
import Button from "@material-ui/core/Button";

const CategorySelect = ({categories, submitCats}) => {
    const [selectedOptions, setSelectedOptions] = useState([]);
    let handleChange = (e) => {
        let value = Array.from(e.target.selectedOptions, option => option.value);
        setSelectedOptions(value);
        console.log(value);
        submitCats(value);
    }

    let renderCategory = (option) => {
        return (<option key={option.id} value={option.id}>{option.name}</option>);
    }

    return (
        <>
            <select onChange={handleChange} value={selectedOptions} multiple={true} >
                {categories.map(option =>
                    renderCategory(option)
                )}
                {/*<option key={null} value={null}>*/}
                {/*    {null}*/}
                {/*</option>*/}
            </select>
        </>
    )
}
export default CategorySelect