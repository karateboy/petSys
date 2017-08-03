<template>
    <div>
        <br>
        <store :store='emptyStore()' :isNew='true'></store>
    </div>
</template>
<style>
    body{
        background-color:#ff0000;
    }

</style>
<script>
    import Store from './Store.vue'
    import {mapGetters} from 'vuex'
    export default{
        data(){
            return {
                msg: 'hello vue'
            }
        },
        computed:{
            ...mapGetters(['user'])
        },
        methods:{
            emptyStore:()=>{
                return {
                    _id:0,
                    name:"",
                    addr:"",
                    phone:""
                }
            }
         },
        components: {
            Store
        }
    }
</script>
