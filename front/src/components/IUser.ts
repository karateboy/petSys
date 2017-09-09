export default interface IUser {
    readonly _id: string,
    readonly company: string,
    readonly name: string,
    readonly password: string,
    readonly phone: string, 
    readonly email: string, 
    readonly groupID: string, 
    readonly storeList: Array<Number>
}

export const emptyUser:IUser = {
    _id: "",
    company: "",
    name: "",
    password: "",
    phone: "", 
    email: "", groupID: "", 
    storeList: []
}