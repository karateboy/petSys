<template>
    <div>
        <br>
        <order-detail></order-detail>
    </div>
</template>
<style scoped>
</style>
<script>
    import OrderDetail from './OrderDetail.vue'
    import {mapActions, mapGetters} from 'vuex'

    export default{
        data(){
            return {}
        },
        mounted:function(){
          this.newOrderWithCustomer({customer:this.customer, storeList:this.storeList})
        },
        computed: {
            ...mapGetters(['order', 'customer', 'storeList'])
        },
        methods: {
            ...mapActions(['newOrderWithCustomer'])
        },
        components: {
            OrderDetail
        }
    };
</script>
