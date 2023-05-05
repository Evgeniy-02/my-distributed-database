package ru.bmstu.labs.MainNode.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestDiscoveryController {
	
	@Autowired
    private DiscoveryClient discoveryClient;

	
	@GetMapping("/show_nodes")
	String showNodes() {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("node");
        System.out.println(serviceInstances/*.stream().map(ServiceInstance::getUri).collect(Collectors.toList())*/);
        return "";
	}

}
