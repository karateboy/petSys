<template>
    <div>
        <br>
        <customer></customer>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang="ts">
import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import { Component, Inject, Model, Prop, Vue, Watch } from 'vue-property-decorator'

import Customer from './Customer.vue'
import { mapGetters } from 'vuex'

export default class AddCustomer extends Vue{
    @Action("emptyCustomer") emptyCustomer:()=>void
    mounted() {
        this.emptyCustomer()
        //this.$store.dispatch('emptyCustomer')
    }

    components: {
        Customer:Customer
    }
}
</script>
