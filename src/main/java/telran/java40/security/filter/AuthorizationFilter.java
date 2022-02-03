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

import telran.java40.accounting.dao.AccountRepository;
import telran.java40.accounting.model.Account;
import telran.java40.forum.dao.PostRepository;
import telran.java40.forum.model.Post;

@Service
@Order(20)
public class AuthorizationFilter implements Filter {

	AccountRepository userRepository;
	PostRepository forumRepository;

	@Autowired
	public AuthorizationFilter(AccountRepository userRepository, PostRepository forumRepository) {
		this.userRepository = userRepository;
		this.forumRepository = forumRepository;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
//		Administrator`s endPoint
		if (isEndPointMatch(request.getServletPath(), "/account/user/\\w+/role/\\w+/?")) {
			Account userAccount = userRepository.findById(request.getUserPrincipal().getName()).get();
			if (!userAccount.getRoles().contains("Administrator".toUpperCase())) {
				response.sendError(403, "Admin rights required");
				return;
			}
		}
		
//		Update User or Delete User
		if (isEndPointMatch(request.getServletPath(), "/account/user/\\w+/?")) {
			Account userAccount = userRepository.findById(request.getUserPrincipal().getName()).get();
			if (!request.getUserPrincipal().getName().equals(getParamFromPath(request.getServletPath()))) {
				if ("PUT".equalsIgnoreCase(request.getMethod())) {
					response.sendError(403);
					return;
				}
				if (!userAccount.getRoles().contains("Administrator".toUpperCase())) {
					response.sendError(403, "Admin rights required");
					return;
				}
			}
		}
		
//		Add Post, Delete Post, Update Post
		if (isEndPointMatch(request.getServletPath(), "/forum/post/\\w+/?")) {
			if ("POST".equalsIgnoreCase(request.getMethod())) {
				if (!request.getUserPrincipal().getName().equals(getParamFromPath(request.getServletPath()))) {
					response.sendError(403);
					return;
				}
			}
			if ("DELETE".equalsIgnoreCase(request.getMethod())) {
				Post post = forumRepository.findById(getParamFromPath(request.getServletPath())).orElse(null);
				if (post == null) {
					response.sendError(403, "Not found");
					return;
				}
				if (!request.getUserPrincipal().getName().equals(post.getAuthor())) {
					Account userAccount = userRepository.findById(request.getUserPrincipal().getName()).get();
					if (!userAccount.getRoles().contains("Moderator".toUpperCase())) {
						response.sendError(403);
						return;
					}
				}
			}
			if ("PUT".equalsIgnoreCase(request.getMethod())) {
				Post post = forumRepository.findById(getParamFromPath(request.getServletPath())).orElse(null);
				if (post == null) {
					response.sendError(403, "Not found");
					return;
				}
				if (!request.getUserPrincipal().getName().equals(post.getAuthor())) {
					response.sendError(403);
					return;
				}
			}
		}
		
//		Add Comment
		if (isEndPointMatch(request.getServletPath(), "/forum/post/\\w+/comment/\\w+/?")) {
			if (!request.getUserPrincipal().getName().equals(getParamFromPath(request.getServletPath()))) {
				response.sendError(403);
				return;
			}
			
		}
		
		chain.doFilter(request, response);
	}

	private String getParamFromPath(String servletPath) {
		String[] pathArray = servletPath.split("/");
		return pathArray[pathArray.length - 1].length() > 0 ? 
				pathArray[pathArray.length - 1] :
				pathArray[pathArray.length - 2];
	}

	private boolean isEndPointMatch(String servletPath, String regex) {
		return servletPath.matches(regex);
	}

}