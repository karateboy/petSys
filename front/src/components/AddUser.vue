<template>
    <div>
        <br>
        <user :user='emptyUser()' :isNew='true'></user>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang='ts'>
import User from './User.vue'
import Vue from 'vue'
import Component from 'vue-class-component'
import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import IUser from './IUser'

@Component
export default class AddUser extends Vue {
    @Getter('user') user: IUser
    emptyUser() {
        return {
            _id: "",
            storeList: JSON.parse(JSON.stringify(this.user.storeList)),
            company: this.user.company,
            groupID: "Clerk"
        }
    }
    components: {
        User: User
    }
}
</script>
