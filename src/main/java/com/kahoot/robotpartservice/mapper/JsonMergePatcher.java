package com.kahoot.robotpartservice.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;

@Component
public class JsonMergePatcher {

	private final ObjectMapper mapper;

	private final Validator validator;

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonMergePatcher.class);


	@Autowired
    public JsonMergePatcher(ObjectMapper mapper, Validator validator) {
		this.mapper = mapper;
		this.validator = validator;
	}


	/**
	 * Will apply a json merge patch (RFC-7386) defined by https://tools.ietf.org/html/rfc7386
	 * And execute JSR-303 Validation on result
	 *
	 * @param json          partial JSON
	 * @param target        pojo that should be patched
	 * @param bindingResult result where errors should be added
	 * @param <T>           Type of target
	 * @return patched and validated target object
	 * @throws BindException if validation fails
	 */
	public <T> Optional mergePatchAndValidate(String json, @NotNull @Valid T target, BindingResult bindingResult) throws BindException {
		Optional patchedOptional = mergePatch(json, target);
		if (patchedOptional.isPresent()) {
			Object patchedResource = patchedOptional.get();
			Errors errors = new BeanPropertyBindingResult(patchedResource, bindingResult.getObjectName());
			validator.validate(patchedResource, errors);
			if (!errors.getAllErrors().isEmpty()) {
				bindingResult.addAllErrors(errors);
				throw new BindException(bindingResult);
			}
		}
		return patchedOptional;
	}


	private <T> Optional mergePatch(String json, @NotNull @Valid T target) {
		JsonNode patchedNode = null;
		try {
			final JsonMergePatch patch = mapper.readValue(json, JsonMergePatch.class);
			patchedNode = patch.apply(mapper.convertValue(target, JsonNode.class));
		}
		catch (IOException | JsonPatchException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return Optional.ofNullable(mapper.convertValue(patchedNode, target.getClass()));
	}
}
