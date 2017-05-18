var app = angular.module('app', ['ngRoute','ngResource']);
app.config(function($routeProvider){
    $routeProvider
        .when('/gallery',{
            templateUrl: 'resources/static/views/gallery.html',
            controller: 'galleryController'
        })
        .when('/contactus',{
            templateUrl: 'resources/static/views/contactus.html',
            controller: 'contactusController'
        })
        .otherwise(
            { redirectTo: '/'}
        );
});

