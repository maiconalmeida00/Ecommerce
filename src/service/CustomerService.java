package service;

import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Customer;
import model.Order;
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
        validarDuplicidade(c);
        c.setActive(true);
        return customerRepository.save(c);
    }

    public Customer atualizar(Customer c) {
        if (c.getId() == null) {
            throw new BusinessException("ID do cliente é obrigatório para atualização");
        }
        validar(c);
        validarDuplicidade(c);
        return customerRepository.save(c);
    }

    public void deletar(Long id) {
        Customer cliente = buscarPorId(id);

        customerRepository.deleteById(cliente.getId());

        List<Order> pedidosDoCliente = orderRepository.findByCustomer(id);
        for (Order pedido : pedidosDoCliente) {
            orderRepository.deleteById(pedido.getId());
        }
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
        if (c.getCpf() == null || c.getCpf().isBlank()) {
            throw new BusinessException("CPF do cliente é obrigatório");
        }
        if (c.getPhone() == null || c.getPhone().isBlank()) {
            throw new BusinessException("Telefone do cliente é obrigatório");
        }
        if (!c.getCpf().matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new BusinessException("CPF deve estar no formato 123.456.789-10");
        }
        if (!c.getPhone().matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
            throw new BusinessException("Telefone deve estar no formato (11) 91111-1111");
        }
    }

    private void validarDuplicidade(Customer clienteAtual) {
        String emailAtual = clienteAtual.getEmail() == null ? "" : clienteAtual.getEmail().trim().toLowerCase();
        String cpfAtualNormalizado = normalizarCpf(clienteAtual.getCpf());

        for (Customer existente : customerRepository.findAll()) {
            if (mesmoRegistro(clienteAtual, existente)) {
                continue;
            }

            String emailExistente = existente.getEmail() == null ? "" : existente.getEmail().trim().toLowerCase();
            if (!emailAtual.isBlank() && emailAtual.equals(emailExistente)) {
                throw new BusinessException("Já existe cliente cadastrado com este e-mail");
            }

            String cpfExistenteNormalizado = normalizarCpf(existente.getCpf());
            if (!cpfAtualNormalizado.isBlank() && cpfAtualNormalizado.equals(cpfExistenteNormalizado)) {
                throw new BusinessException("Já existe cliente cadastrado com este CPF");
            }
        }
    }

    private boolean mesmoRegistro(Customer atual, Customer existente) {
        return atual.getId() != null && atual.getId().equals(existente.getId());
    }

    private String normalizarCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }
}