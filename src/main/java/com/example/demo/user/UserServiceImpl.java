package com.example.demo.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           AddressRepository addressRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse registerUser(UserRequest request) {
        // Check if email or username already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken: " + request.getUsername());
        }

        User user = userMapper.toEntity(request);
        
        // Assign default ROLE_USER
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
        user.addRole(userRole);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findUserById(id);

        // Check if email is being changed and is not already taken
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already taken: " + request.getEmail());
            }
        }

        // Check if username is being changed and is not already taken
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already taken: " + request.getUsername());
            }
        }

        userMapper.updateEntity(user, request);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public void enableUser(Long id) {
        userRepository.updateEnabledStatus(id, true);
    }

    @Override
    public void disableUser(Long id) {
        userRepository.updateEnabledStatus(id, false);
    }

    @Override
    public void lockUser(Long id) {
        userRepository.updateLockedStatus(id, true);
    }

    @Override
    public void unlockUser(Long id) {
        userRepository.updateLockedStatus(id, false);
    }

    @Override
    public void verifyUserEmail(Long id) {
        userRepository.verifyEmail(id);
    }

    @Override
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = findUserById(userId);

        // If this address is set as default, clear any existing default
        if (request.isDefault()) {
            addressRepository.clearDefaultAddress(userId);
        }

        Address address = userMapper.toAddressEntity(request, user);
        Address savedAddress = addressRepository.save(address);
        return new AddressResponse(savedAddress);
    }

    @Override
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));

        // If this address is set as default, clear any existing default
        if (request.isDefault()) {
            addressRepository.clearDefaultAddress(userId);
        }

        userMapper.updateAddressEntity(address, request);
        Address updatedAddress = addressRepository.save(address);
        return new AddressResponse(updatedAddress);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        addressRepository.deleteByIdAndUserId(addressId, userId);
    }

    @Override
    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(AddressResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse getDefaultAddress(Long userId) {
        Address defaultAddress = addressRepository.findDefaultAddressByUserId(userId)
                .orElse(null);
        return defaultAddress != null ? new AddressResponse(defaultAddress) : null;
    }

    @Override
    public void assignRoleToUser(Long userId, RoleName roleName) {
        User user = findUserById(userId);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        if (!user.getRoles().contains(role)) {
            user.addRole(role);
            userRepository.save(user);
        }
    }

    @Override
    public void removeRoleFromUser(Long userId, RoleName roleName) {
        User user = findUserById(userId);
        user.getRoles().removeIf(role -> role.getName().equals(roleName));
        userRepository.save(user);
    }

    @Override
    public long countTotalUsers() {
        return userRepository.count();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::isEnabled)
                .count();
    }

    @Override
    public long countUsersRegisteredBetween(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);
        return userRepository.countUsersRegisteredBetween(start, end);
    }

    // Helper methods
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}