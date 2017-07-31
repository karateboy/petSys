<template>
    <div>
        <br>
        <customer :isNew='true'></customer>
    </div>
</template>
<style>
    body{
        background-color:#ff0000;
    }

</style>
<script>
    import Customer from './Customer.vue'
    import {mapGetters} from 'vuex'

    export default{
        data(){
            return {
            }
        },
        computed:{
            ...mapGetters(['user'])
        },
        methods:{

        },
        components: {
            Customer
        }
    }
</script>
