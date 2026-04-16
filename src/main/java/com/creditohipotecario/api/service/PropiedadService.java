package com.creditohipotecario.api.service;

import com.creditohipotecario.api.dto.PropiedadRequest;
import com.creditohipotecario.api.dto.PropiedadResponse;
import com.creditohipotecario.api.model.EstadoPropiedad;
import com.creditohipotecario.api.model.Propiedad;
import com.creditohipotecario.api.model.TipoPropiedad;
import com.creditohipotecario.api.repository.PropiedadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;

    public PropiedadResponse crear(PropiedadRequest request) {
        Propiedad propiedad = Propiedad.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .direccion(request.getDireccion())
                .comuna(request.getComuna())
                .region(request.getRegion())
                .precio(request.getPrecio())
                .superficieM2(request.getSuperficieM2())
                .dormitorios(request.getDormitorios())
                .banos(request.getBanos())
                .tipo(request.getTipo())
                .build();

        return PropiedadResponse.from(propiedadRepository.save(propiedad));
    }

    public Page<PropiedadResponse> listar(Pageable pageable) {
        return propiedadRepository.findAll(pageable)
                .map(PropiedadResponse::from);
    }

    public Page<PropiedadResponse> filtrar(
            EstadoPropiedad estado,
            TipoPropiedad tipo,
            String comuna,
            BigDecimal precioMin,
            BigDecimal precioMax,
            Integer dormitorios,
            Pageable pageable) {

        return propiedadRepository.filtrar(
                estado, tipo, comuna, precioMin, precioMax, dormitorios, pageable)
                .map(PropiedadResponse::from);
    }

    public PropiedadResponse obtener(Long id) {
        return propiedadRepository.findById(id)
                .map(PropiedadResponse::from)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada: " + id));
    }

    public PropiedadResponse actualizar(Long id, PropiedadRequest request) {
        Propiedad propiedad = propiedadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada: " + id));

        propiedad.setTitulo(request.getTitulo());
        propiedad.setDescripcion(request.getDescripcion());
        propiedad.setDireccion(request.getDireccion());
        propiedad.setComuna(request.getComuna());
        propiedad.setRegion(request.getRegion());
        propiedad.setPrecio(request.getPrecio());
        propiedad.setSuperficieM2(request.getSuperficieM2());
        propiedad.setDormitorios(request.getDormitorios());
        propiedad.setBanos(request.getBanos());
        propiedad.setTipo(request.getTipo());

        return PropiedadResponse.from(propiedadRepository.save(propiedad));
    }

    public PropiedadResponse cambiarEstado(Long id, EstadoPropiedad nuevoEstado) {
        Propiedad propiedad = propiedadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada: " + id));

        propiedad.setEstado(nuevoEstado);
        return PropiedadResponse.from(propiedadRepository.save(propiedad));
    }

    public void eliminar(Long id) {
        if (!propiedadRepository.existsById(id)) {
            throw new RuntimeException("Propiedad no encontrada: " + id);
        }
        propiedadRepository.deleteById(id);
    }
}