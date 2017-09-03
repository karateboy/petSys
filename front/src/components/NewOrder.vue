<template>
    <div>
        <br>
        <order-detail></order-detail>
    </div>
</template>
<style scoped>

</style>
<script lang='ts'>
import OrderDetail from './OrderDetail.vue'
import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import { Component, Inject, Model, Prop, Vue, Watch } from 'vue-property-decorator'
import IStore from './IStore'

export default class NewOrder extends Vue{
    mounted() {
        this.newOrderWithCustomer({ customer: this.customer, storeList: this.storeList })
    }
    @Getter('order') order:Object
    @Getter('customer') customer:Object
    @Getter('storeList') storeList:Array<IStore>
    @Action("newOrderWithCustomer") newOrderWithCustomer:(payload:Object)=>void
    components: {
        OrderDetail
    }
};
</script>
