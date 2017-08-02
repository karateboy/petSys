<template>
    <div>
        <br>
        <customer :isNew='true' :customerParam="emptyCustomer()"></customer>
    </div>
</template>
<style>
    body {
        background-color: #ff0000;
    }

</style>
<script>
    import Customer from './Customer.vue'
    import {mapGetters} from 'vuex'

    export default {
        data() {
            return {}
        },
        computed: {
            ...mapGetters(['user'])
        },
        methods: {
            emptyCustomer:() => {
                return {
                    _id: 0,
                    petList: [],
                    orderList: [],
                    firstTime: 0,
                    lastTime: 0
                }
            }
        },
        components: {
            Customer
        }
    }
</script>
