import { IPet } from './ICustomer'
export default interface Order {
    _id: number
    customerID: number
    pet: IPet
    storeID: number
    services: Array<string>
    note: string
    workers: Array<string>
    time: number
    active: boolean
}