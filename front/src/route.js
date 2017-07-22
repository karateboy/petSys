import Dashboard from "./components/Dashboard.vue"
import Login from "./components/Login.vue"
import UserManagement from './components/UserManagement.vue'
import AddUser from './components/AddUser.vue'
import DelUser from './components/DelUser.vue'
import UpdateUser from './components/UpdateUser.vue'

export const routes = [
    {path: '/', component: Dashboard, name: 'Dashboard'},
    {path: '/Login', component: Login, name: 'Login'},
    {
        path: '/User', component: UserManagement, name: 'UserManagement',
        children: [
            {path: 'AddUser', component:AddUser, name:'AddUser' },
            {path: 'DelUser', component:DelUser, name:'DelUser' },
            {path: 'UpdateUser', component:UpdateUser, name:'UpdateUser' },
        ]
    },
    {path: '*', redirect: '/'}
];
