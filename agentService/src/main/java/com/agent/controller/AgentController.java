package com.agent.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.client.RestTemplate;

import com.agent.entity.Agent;
import com.agent.exception.ResourceNotFoundException;
import com.agent.repo.AgentRepo;

@RestController
@RequestMapping("/agentApi")
public class AgentController {
	@Autowired
	AgentRepo agentRepo;
	
	@GetMapping("/allAgents")
    public List<Agent> getAllAgents() {
        return agentRepo.findAll();
    }
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${getClients}")
	public String clientLink;
	
    @GetMapping("/agent/{id}")
    public ResponseEntity<Agent> getAgentById(@PathVariable(value = "id") int agentId)
        throws ResourceNotFoundException {
        Agent agent = agentRepo.findById(agentId)
          .orElseThrow(() -> new ResourceNotFoundException("Agent not found for this id :: " + agentId));
        List clients=this.restTemplate.getForObject(clientLink+agent.getAgentid(), List.class);
        agent.setClients(clients);
        return ResponseEntity.ok().body(agent);
    }
    
    @GetMapping("/agentEmail/{id}")
    public String getAgentEmailId(@PathVariable(value = "id") int agentId)
        throws ResourceNotFoundException {
        Agent agent = agentRepo.findById(agentId)
          .orElseThrow(() -> new ResourceNotFoundException("Agent not found for this id :: " + agentId));
        return agent.getEmail();
    }
    
    @PostMapping("/agentRegistration")
    public ResponseEntity<EntityModel<Agent>> createAgent(@Valid @RequestBody Agent agent) throws ResourceNotFoundException {
    	agentRepo.save(agent);
    	EntityModel<Agent> resource = EntityModel.of(agent);
    	WebMvcLinkBuilder linkforAllClints = WebMvcLinkBuilder.linkTo(
		         WebMvcLinkBuilder.methodOn(this.getClass()).getAllAgents());


		WebMvcLinkBuilder linkforClintByName = WebMvcLinkBuilder.linkTo(
		         WebMvcLinkBuilder.methodOn(this.getClass()).getAgentById(agent.getAgentid()));

		// add link to the resource
		resource.add(linkforAllClints.withRel("Click here to Get All Agents"));
		resource.add(linkforClintByName.withRel("Click here to get Agent Info"));
		return new ResponseEntity<EntityModel<Agent>>(resource,HttpStatus.OK);
  
    }

    @PutMapping("/agentUpdate/{id}")
    public ResponseEntity<Agent> updateAgent(@PathVariable(value = "id") int agentId,
         @Valid @RequestBody Agent agentDetails) throws ResourceNotFoundException {
        Agent agent = agentRepo.findById(agentId)
        .orElseThrow(() -> new ResourceNotFoundException("Agent not found for this id :: " + agentId));

        agent.setAgentName(agentDetails.getAgentName());
        agent.setAgentState(agentDetails.getAgentState());
        agent.setLinkedBankAccountName(agentDetails.getLinkedBankAccountName());
        final Agent updatedAgent = agentRepo.save(agent);
        return ResponseEntity.ok(updatedAgent);
    }
    @PutMapping("/agentWBUpdate/{id}/{wb}")
	public ResponseEntity<Agent> updateWalletBalanceAgent(@PathVariable(value = "id") int agentId,
			@PathVariable(value = "wb") int walletBalance) throws ResourceNotFoundException {
		Agent agent = agentRepo.findById(agentId)
				.orElseThrow(() -> new ResourceNotFoundException("Agent not found for this id :: " + agentId));
        agent.setWalletBalance(walletBalance);
        final Agent updatedAgent = agentRepo.save(agent);
        return ResponseEntity.ok(updatedAgent);
    }
    

    @DeleteMapping("/agentDelete/{id}")
    public Map<String, Boolean> deleteAgent(@PathVariable(value = "id") int agentId)
         throws ResourceNotFoundException {
        Agent agent = agentRepo.findById(agentId)
       .orElseThrow(() -> new ResourceNotFoundException("Agent not found for this id :: " + agentId));

        agentRepo.delete(agent);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}