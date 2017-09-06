export interface IPet{
    name: string
    breed: string
    color?: string
    bdate?: number
    habit?: string
    hospital?: string
    chip?: number
    vacineList?: string
}

export interface ICustomer {
    _id: number 
    name: string
    bdate?: number
    addr?: string
    phone: String
    email?: string
    facebook?: string
    line?: string
    note?: string
    petList: Array<IPet>
    orderList: Array<number>
    firstTime: number
    lastTime:number
}