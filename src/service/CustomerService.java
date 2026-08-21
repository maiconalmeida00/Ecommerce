package service;

import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Customer;
import repository.CustomerRepository;
import repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    public Customer criar(Customer c) {
        validar(c);
        return customerRepository.save(c);
    }

    public Customer atualizar(Customer c) {
        if (c.getId() == null) {
            throw new BusinessException("ID do cliente é obrigatório para atualização");
        }
        validar(c);
        return customerRepository.save(c);
    }

    public void deletar(Long id) {
        if (orderRepository.countByCustomer(id) > 0) {
            throw new BusinessException("Não é possível excluir o cliente porque existem pedidos vinculados");
        }
        customerRepository.deleteById(id);
    }

    public Customer buscarPorId(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));
    }

    public List<Customer> listarTodos() {
        return customerRepository.findAll();
    }

    public List<Customer> buscarPorNomeContendo(String trecho) {
        return customerRepository.findAll().stream()
                .filter(c -> c.getName() != null &&
                        c.getName().toLowerCase().contains(trecho.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void validar(Customer c) {
        if (c.getName() == null || c.getName().isBlank()) {
            throw new BusinessException("Nome do cliente é obrigatório");
        }
        if (c.getEmail() == null || c.getEmail().isBlank()) {
            throw new BusinessException("E-mail do cliente é obrigatório");
        }
    }
}
