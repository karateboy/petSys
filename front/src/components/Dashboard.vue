<template>
    <div>
        <h1>寵物店客戶管理系統</h1>
    </div>
</template>
<style>

</style>
<script lang='ts'>
import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import { Component, Inject, Model, Prop, Vue, Watch } from 'vue-property-decorator'
import IUser from './IUser'

@Component
export default class Dashboard extends Vue {
    @Getter('isAuthenticated') isAuthenticated: boolean
    @Getter('user') user: IUser
    @Action('authenticate') authenticate: () => void
}
</script>
