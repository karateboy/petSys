import Dashboard from "./components/Dashboard.vue"
import Login from "./components/Login.vue"
import RegisterCompanyOwner from './components/RegisterCompanyOwner.vue'
//User
import UserManagement from './components/UserManagement.vue'
import AddUser from './components/AddUser.vue'
import DelUser from './components/DelUser.vue'
import UpdateUser from './components/UpdateUser.vue'
//Store
import StoreManagement from './components/StoreManagement.vue'
import AddStore from  './components/AddStore.vue'
import DelStore from './components/DelStore.vue'
import UpdateStore from './components/UpdateStore.vue'
//Order
import NewOrder from "./components/NewOrder.vue"
import Order from "./components/Order.vue"
import QueryOrder from "./components/QueryOrder.vue"
//Customer
import CustomerManagement from './components/CustomerManagement.vue'
import AddCustomer from './components/AddCustomer.vue'
import QueryCustomer from './components/QueryCustomer.vue'
//Hotel

//Report
import Report from './components/Report.vue'

export const routes = [
    {path: '/', component: Dashboard, name: 'Dashboard'},
    {path: '/Login', component: Login, name: 'Login'},
    {path: '/RegisterCompany', component: RegisterCompanyOwner, name: 'RegisterCompany'},
    {
        path: '/Order', component: Order, name: 'Order',
        children: [
            {path: 'New', component: NewOrder, name: 'NewOrder'},
            {path: 'Query', component: QueryOrder}
        ]
    },
    {
        path: '/Customer', component: CustomerManagement, name: 'CustomerManagement',
        children: [
            {path: 'Add', component:AddCustomer, name:'AddCustomer' },
            {path: 'Query', component: QueryCustomer, name:'QueryCustomer'}
        ]
    },
    {
        path: '/User', component: UserManagement, name: 'UserManagement',
        children: [
            {path: 'AddUser', component:AddUser, name:'AddUser' },
            {path: 'DelUser', component:DelUser, name:'DelUser' },
            {path: 'UpdateUser', component:UpdateUser, name:'UpdateUser' },
        ]
    },
    {
        path: '/Store', component: StoreManagement, name: 'StoreManagement',
        children: [
            {path: 'AddStore', component:AddStore, name:'AddStore' },
            {path: 'DelStore', component:DelStore, name:'DelStore' },
            {path: 'UpdateStore', component:UpdateStore, name:'UpdateStore' },
        ]
    },
    {
        path: '/Report', component: Report, name: 'Report',
        children: [
            {path: 'Report1', component:AddStore, name:'Report1' },
        ]
    },
    {path: '*', redirect: '/'}
];
