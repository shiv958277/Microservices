package com.client.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.client.dto.ClientDTO;
import com.client.entity.Client;
import com.client.exception.ResourceNotFoundException;
import com.client.repo.ClientRepo;
import com.client.util.ObjectMapperUtils;


@RestController
@RequestMapping("/clientApi")
public class ClientController {
	@Autowired
	ClientRepo clientRepo;
	
	@GetMapping("/allClients")
    public List<Client> getAllclients() {
        return clientRepo.findAll();
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<Client> getclientById(@PathVariable(value = "id") int clientId)
        throws ResourceNotFoundException {
        Client client = clientRepo.findById(clientId)
          .orElseThrow(() -> new ResourceNotFoundException("client not found for this id :: " + clientId));
        return ResponseEntity.ok().body(client);
    }
    @GetMapping("/agent/{id}")
    public List<Client> getAgentById(@PathVariable(value = "id") int agentId) {
    	List<Client> list=clientRepo.findAll();
    	return list.stream().filter(client->client.getAgentId()==agentId).collect(Collectors.toList());
    }
    @GetMapping("/agentDto/{id}")
    public List<ClientDTO> getAgentByIdDTo(@PathVariable(value = "id") int agentId) {
    	List<Client> list=clientRepo.findAll();
    	List<Client> filterList=list.stream().filter(client->client.getAgentId()==agentId).collect(Collectors.toList());
    	List<ClientDTO> listOfPostDTO = ObjectMapperUtils.mapAll(filterList, ClientDTO.class);
    	return listOfPostDTO;
    }
    
    @GetMapping("/clientEmail/{id}")
    public String getAgentEmailId(@PathVariable(value = "id") int agentId)
        throws ResourceNotFoundException {
        Client agent = clientRepo.findById(agentId)
          .orElseThrow(() -> new ResourceNotFoundException("Client not found for this id :: " + agentId));
        return agent.getEmail();
    }
    
    @PostMapping("/clientRegistration")
    public ResponseEntity<EntityModel<Client>> createclient(@Valid @RequestBody Client client) throws ResourceNotFoundException {
    	clientRepo.save(client);
    	EntityModel<Client> resource = EntityModel.of(client);
    	WebMvcLinkBuilder linkforAllClints = WebMvcLinkBuilder.linkTo(
		         WebMvcLinkBuilder.methodOn(this.getClass()).getAllclients());


		WebMvcLinkBuilder linkforClintByName = WebMvcLinkBuilder.linkTo(
		         WebMvcLinkBuilder.methodOn(this.getClass()).getclientById(client.getClientid()));

		// add link to the resource
		resource.add(linkforAllClints.withRel("Click here to Get All clients"));
		resource.add(linkforClintByName.withRel("Click here to get client Info"));
		return new ResponseEntity<EntityModel<Client>>(resource,HttpStatus.OK);
  
    }

    @PutMapping("/clientUpdate/{id}")
    public ResponseEntity<Client> updateclient(@PathVariable(value = "id") int clientId,
         @Valid @RequestBody Client clientDetails) throws ResourceNotFoundException {
        Client client = clientRepo.findById(clientId)
        .orElseThrow(() -> new ResourceNotFoundException("client not found for this id :: " + clientId));

        client.setClientName(clientDetails.getClientName());
        client.setClientState(clientDetails.getClientState());
        client.setBankAccountName(clientDetails.getBankAccountName());
        final Client updatedclient = clientRepo.save(client);
        return ResponseEntity.ok(updatedclient);
    }
    @PutMapping("/clientWBUpdate/{id}/{wb}")
	public ResponseEntity<Client> updateWalletBalanceAgent(@PathVariable(value = "id") int clientId,
			@PathVariable(value = "wb") int walletBalance) throws ResourceNotFoundException {
		Client agent = clientRepo.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found for this id :: " + clientId));
        agent.setWalletBalance(walletBalance);
        final Client updatedAgent = clientRepo.save(agent);
        return ResponseEntity.ok(updatedAgent);
    }

    @DeleteMapping("/clientDelete/{id}")
    public Map<String, Boolean> deleteclient(@PathVariable(value = "id") int clientId)
         throws ResourceNotFoundException {
        Client client = clientRepo.findById(clientId)
       .orElseThrow(() -> new ResourceNotFoundException("client not found for this id :: " + clientId));

        clientRepo.delete(client);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}