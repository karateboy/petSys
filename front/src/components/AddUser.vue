<template>
    <div>
        <br>
        <user :user='emptyUser()' :isNew='true'></user>
    </div>
</template>
<style>
    body{
        background-color:#ff0000;
    }

</style>
<script>
    import User from './User.vue'
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
          emptyUser(){
              return {
                  _id:"",
                  storeList: JSON.parse(JSON.stringify(this.user.storeList)),
                  company: this.user.company,
                  groupID:"Clerk"
              }
          }
        },
        components: {
            User
        }
    }
</script>
