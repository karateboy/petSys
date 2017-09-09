<template>
    <div>
        <br>
        <user :user='user' :isNew='true' :registerCompany="true"></user>
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
import { emptyUser } from './IUser'

@Component({
    components: {
        User: User
    }
})
export default class RegisterCompanyOwner extends Vue {
    user = emptyUser
}
</script>
