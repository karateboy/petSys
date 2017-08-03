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
<script>
    import Customer from './Customer.vue'
    import {mapGetters} from 'vuex'

    export default {
        data() {
            return {}
        },
        mounted: function(){
            this.$store.dispatch('emptyCustomer')
        },
        components: {
            Customer
        }
    }
</script>
