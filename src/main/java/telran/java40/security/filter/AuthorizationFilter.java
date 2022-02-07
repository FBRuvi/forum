package telran.java40.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.java40.forum.dao.PostRepository;
import telran.java40.forum.model.Post;
import telran.java40.security.SecurityContext;
import telran.java40.security.UserProfile;

@Service
@Order(20)
public class AuthorizationFilter implements Filter {

//	AccountRepository userRepository;
	SecurityContext securityContext;
	PostRepository forumRepository;

	@Autowired
	public AuthorizationFilter(SecurityContext securityContext, PostRepository forumRepository) {
		this.securityContext = securityContext;
		this.forumRepository = forumRepository;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
//		Administrator`s endPoint (change roles)
		if (isEndPointMatch(request.getServletPath(), "/account/user/\\w+/role/\\w+/?")) {
			UserProfile userProfile = securityContext.getUser(request.getUserPrincipal().getName());
			if (!userProfile.getRoles().contains("Administrator".toUpperCase())) {
				response.sendError(403, "Admin rights required");
				return;
			}
		}
		
//		Update User or Delete User
		if (isEndPointMatch(request.getServletPath(), "/account/user/\\w+/?")) {
			UserProfile userProfile = securityContext.getUser(request.getUserPrincipal().getName());
			if (!request.getUserPrincipal().getName().equals(getParamFromPath(request.getServletPath()))) {
				if ("PUT".equalsIgnoreCase(request.getMethod())) {
					response.sendError(403);
					return;
				}
				if (!userProfile.getRoles().contains("Administrator".toUpperCase())) {
					response.sendError(403, "Admin rights required");
					return;
				}
			}
		}
		
//		Add Post, Delete Post, Update Post
		if (isEndPointMatch(request.getServletPath(), "/forum/post/\\w+/?")) {
			if ("POST".equalsIgnoreCase(request.getMethod())) {
				if (!request.getUserPrincipal().getName().equals(getParamFromPath(request.getServletPath()))) {
					response.sendError(403, "Authorization error");
					return;
				}
			}
			if ("DELETE".equalsIgnoreCase(request.getMethod())) {
				Post post = forumRepository.findById(getParamFromPath(request.getServletPath())).orElse(null);
				if (post == null) {
					response.sendError(403, "Post not found");
					return;
				}
				if (!request.getUserPrincipal().getName().equals(post.getAuthor())) {
					UserProfile userProfile = securityContext.getUser(request.getUserPrincipal().getName());
					if (!userProfile.getRoles().contains("Moderator".toUpperCase())) {
						response.sendError(403, "Moderator rights required");
						return;
					}
				}
			}
			if ("PUT".equalsIgnoreCase(request.getMethod())) {
				Post post = forumRepository.findById(getParamFromPath(request.getServletPath())).orElse(null);
				if (post == null) {
					response.sendError(403, "Post not found");
					return;
				}
				if (!request.getUserPrincipal().getName().equals(post.getAuthor())) {
					response.sendError(403, "Forbidden to change");
					return;
				}
			}
		}
		
//		Add Comment (From own name only)
		if (isEndPointMatch(request.getServletPath(), "/forum/post/\\w+/comment/\\w+/?")) {
			if (!request.getUserPrincipal().getName().equals(getParamFromPath(request.getServletPath()))) {
				response.sendError(403, "Action forbidden");
				return;
			}
			
		}
		
//		Add like (Block for autoLike)
		if (isEndPointMatch(request.getServletPath(), "/forum/post/\\w+/like/?")) {
			Post post = forumRepository.findById(getParamFromPath(request.getServletPath(), 3)).orElse(null);
			if (post == null) {
				response.sendError(403, "Post not found");
				return;
			}
			if (request.getUserPrincipal().getName().equals(post.getAuthor())) {
				response.sendError(403, "Action forbidden");
				return;
			}
		}
		
		chain.doFilter(request, response);
	}

	private String getParamFromPath(String servletPath, int index) {
		String[] pathArray = servletPath.split("/");
		return pathArray[index];
	}
	
	private String getParamFromPath(String servletPath) {
		return getParamFromPath(servletPath, servletPath.split("/").length - 1);
	}

	private boolean isEndPointMatch(String servletPath, String regex) {
		return servletPath.matches(regex);
	}

}
