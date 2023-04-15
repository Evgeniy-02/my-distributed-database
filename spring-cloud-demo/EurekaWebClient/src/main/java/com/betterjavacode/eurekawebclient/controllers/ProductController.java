package com.betterjavacode.eurekawebclient.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class ProductController
{
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/")
    public String home(Model model)
    {
        List serviceInstances = discoveryClient.getInstances("product" +
                "-service");

        if(serviceInstances != null && !serviceInstances.isEmpty())
        {
            ServiceInstance serviceInstance = (ServiceInstance) serviceInstances.get(0);
            String url = serviceInstance.getUri().toString();
            url = url + "/products";
            RestTemplate restTemplate = new RestTemplate();
            List products = restTemplate.getForObject(url, List.class);
            model.addAttribute("products", products);
        }

        return "home";
    }
    
    @GetMapping("/api/graphql")
    public String graphql(Model model) {
    	List serviceInstances = discoveryClient.getInstances("custom-database");

        if(serviceInstances != null && !serviceInstances.isEmpty())
        {
            ServiceInstance serviceInstance = (ServiceInstance) serviceInstances.get(0);
            String url = serviceInstance.getUri().toString();
            url = url + "/api/graphql";
            
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = 
            	      new HttpEntity<String>("{\"query\":\"query {\\n\\tusers(alias: \\\"1\\\") {\\n\\t\\tid\\n\\t\\tname\\n\\t\\tlastName\\n\\t\\temail\\n\\t}\\n}\\n\"}", headers);

            String response = restTemplate.postForObject(url, request,  String.class);
            model.addAttribute("response", response);
        }
        
        return "graphql";
    }
}
