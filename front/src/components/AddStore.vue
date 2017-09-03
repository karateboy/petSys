<template>
    <div>
        <br>
        <store :store='emptyStore()' :isNew='true'></store>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang='ts'>
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
import Store from './Store.vue'

@Component
export default class AddStore extends Vue {
    @Getter('user') user: IUser
    emptyStore: () =>
        {
            _id: 0,
            name: "",
            addr: "",
            phone: ""
        }

    components: {
        Store:Store
    }
}
</script>
