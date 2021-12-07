package com.fundtransfer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fundtransfer.entity.Agent;
import com.fundtransfer.entity.Client;
import com.fundtransfer.entity.Transactions;
import com.fundtransfer.repo.TransactionRepo;


@RestController
@RequestMapping("/fundtransfer")
public class FundTransferController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	TransactionRepo tRepo;
	
	@Value("${getEmailOfAgent}")
	public String emailAgent;
	
	@Value("${getEmailOfClient}")
	public String emailClient;
	
	@Value("${generateOtp}")
	public String gOtp;
	
	@Value("${validateOtp}")
	public String vOtp;
	
	@Value("${getAgent}")
	public String gAgent;
	
	@Value("${getClient}")
	public String gClient;
	
	@Value("${updateAgent}")
	public String uAgent;
	
	@Value("${updateClient}")
	public String uClient;
	
	@GetMapping("/allTransactions")
    public List<Transactions> getAllTransactions() {
        return tRepo.findAll();
    }
	
	@RequestMapping("/doTransactionAToc/{id}")
	public String doTransactionOfAgent(@PathVariable(value = "id") int aId)
	{
		String mail=this.restTemplate.getForObject(emailAgent+aId, String.class);
		this.restTemplate.postForObject(gOtp+mail,null,void.class);
		return "OTP Sent";
	}
	
	@RequestMapping("/doTransactionCToA/{id}")
	public String doTransactionOfClient(@PathVariable(value = "id") int aId)
	{
		String mail=this.restTemplate.getForObject(emailClient+aId, String.class);
		this.restTemplate.postForObject(gOtp+mail,null,void.class);
		return "OTP Sent";
	}

	@RequestMapping("/transfer-AToC/{aId}/{cId}/{amount}/{otp}")
	public ResponseEntity<?> transferAToC(@PathVariable(value = "aId") int aId,
			@PathVariable(value = "cId") int cId, @PathVariable(value = "amount") int amount,@PathVariable(value = "otp") int otp) {
		try {
		String status=this.restTemplate.getForObject(vOtp+otp, String.class);
		if(status.equals("OTP Verified"))
		{
		Transactions tr = new Transactions();
		Agent agent = this.restTemplate.getForObject(gAgent+aId, Agent.class);
		Client client = this.restTemplate.getForObject(gClient+cId, Client.class);
		agent.setWalletBalance(agent.getWalletBalance() - amount);
		client.setWalletBalance(client.getWalletBalance() + amount);
		tr.setAgentId(agent.getAgentid());
		tr.setClientId(client.getClientid());
		tr.setTAmount(amount);
		tr.setTType("AToC");
		tRepo.save(tr);
		this.restTemplate.put(
				uAgent+ agent.getAgentid() + "/" + agent.getWalletBalance(),
				agent);
		this.restTemplate.put(uClient + client.getClientid() + "/"
				+ client.getWalletBalance(), client);
		EntityModel<Transactions> resource = EntityModel.of(tr);
		WebMvcLinkBuilder linkforAllClints = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllTransactions());

		// add link to the resource
		resource.add(linkforAllClints.withRel("Click here to Get All Transactions"));
		return new ResponseEntity<EntityModel<Transactions>>(resource, HttpStatus.OK);
		}
		else {
			return ResponseEntity.badRequest().body("Access Denied");
		}
		}catch (Exception e)
		{
			throw e;
		}

	}

	@RequestMapping("/transfer-CToA/{aId}/{cId}/{amount}/{otp}")
	public ResponseEntity<?> transferCToA(@PathVariable(value = "aId") int aId,
			@PathVariable(value = "cId") int cId, @PathVariable(value = "amount") int amount,@PathVariable(value = "otp") int otp) {
		String status=this.restTemplate.getForObject(vOtp+otp, String.class);
		if(status.equals("OTP Verified"))
		{
		Agent agent = this.restTemplate.getForObject(gAgent + aId, Agent.class);
		Client client = this.restTemplate.getForObject(gClient + cId, Client.class);
		Transactions tr = new Transactions();
		agent.setWalletBalance(agent.getWalletBalance() + amount);
		client.setWalletBalance(client.getWalletBalance() - amount);
		tr.setAgentId(agent.getAgentid());
		tr.setClientId(client.getClientid());
		tr.setTAmount(amount);
		tr.setTType("CToA");
		tRepo.save(tr);
		this.restTemplate.put(uAgent + agent.getAgentid() + "/" + agent.getWalletBalance(), agent);
		this.restTemplate.put(uClient + client.getClientid() + "/" + client.getWalletBalance(), client);
		EntityModel<Transactions> resource = EntityModel.of(tr);
		WebMvcLinkBuilder linkforAllClints = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllTransactions());

		// add link to the resource
		resource.add(linkforAllClints.withRel("Click here to Get All Transactions"));
		return new ResponseEntity<EntityModel<Transactions>>(resource, HttpStatus.OK);
		}
		else {
			return ResponseEntity.badRequest().body("Access Denied");
		}

	}

}
