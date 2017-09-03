<template>
    <div>
        <br>
        <user :user='emptyUser()' :isNew='true' :registerCompany="true"></user>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang='ts'>
import User from './User.vue'
import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import { Component, Inject, Model, Prop, Vue, Watch } from 'vue-property-decorator'
import IUser from './IUser'

export default class RegisterCompanyOwner extends Vue {
    @Getter('user') user: IUser
    emptyUser: () => {
        _id: ""
        storeList: Array<Object>
        company: ""
        groupID: "Owner"
    }
    components: {
        User: User
    }
}
</script>
