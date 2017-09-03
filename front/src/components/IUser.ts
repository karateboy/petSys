export default interface IUser {
    _id: string,
    company: string,
    name: string,
    password: string,
    phone: string, email: string, groupID: string, 
    storeList: Array<Number>
}