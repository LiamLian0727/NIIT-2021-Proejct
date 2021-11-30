let menu = document.querySelector('#menu-bar');
let navbar = document.querySelector('.navbar');
let header = document.querySelector('.header-2');
//导航栏一直在上面
menu.addEventListener('click', () =>{
    navbar.classList.toggle('active');
});

window.onscroll = () =>{
    navbar.classList.remove('active');

    if(window.scrollY > 150){
        header.classList.add('active');
    }else{
        header.classList.remove('active');
    }

}
