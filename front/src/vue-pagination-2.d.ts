declare module 'vue-pagination-2' {
    class Pagination{

    }

    export namespace PaginationEvent{
        function $on(x:string, fn:(page:number)=>void):void
    }
//declare export Pagination: any
    //import { Pagination, PaginationEvent } from 'vue-pagination-2'
}
//export as namespace vue-pagination-2;
